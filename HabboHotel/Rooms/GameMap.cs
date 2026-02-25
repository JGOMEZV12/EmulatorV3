using System.Drawing;
using Polar.Core;
using Polar.HabboHotel.Items;
using Polar.HabboHotel.Groups;
using Polar.HabboHotel.Rooms.Games.Teams;
using System.Collections.Concurrent;
using Polar.HabboHotel.Pathfinding;
using System.Linq;

namespace Polar.HabboHotel.Rooms
{
    public class Gamemap : IDisposable
    {
        private Room _room;
        private byte[,] _gameMap;
        public bool DiagonalEnabled;
        private RoomModel mStaticModel;
        private RoomModel _staticModel;
        public byte[,] mUserOnMap { get; private set; }
        public double[,] _itemHeightmap;
        private DynamicRoomModel _dynamicModel;
        private ConcurrentDictionary<Point, List<int>> _coordinatedItems;
        public ConcurrentDictionary<Point, List<RoomUser>> _userMap;
        public byte[,] EffectMap { get; private set; }
        public byte[,] mSquareTaking { get; private set; }
        public byte[,] GameMap { get; private set; }

        public Gamemap(Room room)
        {
            _room = room;
            DiagonalEnabled = true;

            mStaticModel = PolarEnvironment.GetGame().GetRoomManager().GetModel(room.ModelName, room.Id);
            if (mStaticModel == null)
                throw new Exception("No modeldata found for roomID " + room.Id);

            _staticModel = mStaticModel;
            _dynamicModel = new DynamicRoomModel(mStaticModel);

            InitializeArrays();

            _userMap = new ConcurrentDictionary<Point, List<RoomUser>>();
            _coordinatedItems = new ConcurrentDictionary<Point, List<int>>();
        }

        private void InitializeArrays()
        {
            int sizeX = Model.MapSizeX;
            int sizeY = Model.MapSizeY;

            GameMap = new byte[sizeX, sizeY];
            mUserOnMap = new byte[sizeX, sizeY];
            mSquareTaking = new byte[sizeX, sizeY];
            EffectMap = new byte[sizeX, sizeY];
            _itemHeightmap = new double[sizeX, sizeY];
        }

        #region User Management
        public void AddUserToMap(RoomUser user, Point coord)
        {
            if (user == null) return;

            _userMap.AddOrUpdate(coord,
                new List<RoomUser> { user },
                (key, existingList) =>
                {
                    if (!existingList.Contains(user))
                        existingList.Add(user);
                    return existingList;
                });

            if (ValidTile(coord.X, coord.Y))
                mUserOnMap[coord.X, coord.Y] = 1;
        }

        public void RemoveUserFromMap(RoomUser user, Point coord)
        {
            if (user == null || !_userMap.ContainsKey(coord)) return;

            _userMap.AddOrUpdate(coord,
                new List<RoomUser>(),
                (key, existingList) =>
                {
                    existingList.RemoveAll(x => x?.VirtualId == user.VirtualId);
                    return existingList.Count > 0 ? existingList : null;
                });

            if (_userMap.TryGetValue(coord, out var list) && (list == null || list.Count == 0))
                _userMap.TryRemove(coord, out _);

            if (ValidTile(coord.X, coord.Y))
                mUserOnMap[coord.X, coord.Y] = 0;
        }

        public void UpdateUserMovement(Point oldCoord, Point newCoord, RoomUser user)
        {
            RemoveUserFromMap(user, oldCoord);
            AddUserToMap(user, newCoord);
        }

        public bool MapGotUser(Point coord)
        {
            return GetRoomUsers(coord).Count > 0;
        }

        public bool MapGotUser(Point coord, bool CheckingInvisible, bool IsInvisible)
        {
            List<RoomUser> users = GetRoomUsers(coord).Where(u => !u.IsBot).ToList();
            if (users == null || users.Count == 0)
                return false;

            if (!CheckingInvisible)
                return users.Count > 0;

            return users.Any(user => IsUserVisible(user, IsInvisible));
        }

        private bool IsUserVisible(RoomUser user, bool isInvisible)
        {
            if (user.IsBot)
            {
                var botRoleplay = user.GetBotRoleplay();
                return botRoleplay != null && !botRoleplay.Invisible;
            }

            var client = user.GetClient();
            var roleplay = client?.GetRoleplay();
            return roleplay != null && (!roleplay.Invisible || isInvisible);
        }

        public List<RoomUser> GetRoomUsers(Point coord)
        {
            return _userMap.TryGetValue(coord, out var users)
                ? new List<RoomUser>(users)
                : new List<RoomUser>();
        }
        #endregion

        #region Teleportation
        public void TeleportToSquare(RoomUser user, Point point)
        {
            if (user == null || !ValidTile(point.X, point.Y)) return;

            UpdateUserStateAndPosition(user, point, GetHeightForSquare(point));
            UpdateUserOrientation(user, point);
            ResetUserMovement(user);
        }

        public void TeleportToItem(RoomUser user, Item item)
        {
            if (user == null || item == null) return;

            var point = new Point(item.GetX, item.GetY);
            UpdateUserStateAndPosition(user, point, item.GetZ);

            user.RotBody = item.Rotation;
            user.RotHead = item.Rotation;

            ResetUserMovement(user);
        }

        private void UpdateUserStateAndPosition(RoomUser user, Point newPoint, double newZ)
        {
            if (ValidTile(user.X, user.Y))
                GameMap[user.X, user.Y] = user.SqState;

            UpdateUserMovement(user.Coordinate, newPoint, user);

            user.X = newPoint.X;
            user.Y = newPoint.Y;
            user.Z = newZ;

            user.SqState = GameMap[newPoint.X, newPoint.Y];
            if (ValidTile(newPoint.X, newPoint.Y))
                GameMap[newPoint.X, newPoint.Y] = 1;
        }

        private void UpdateUserOrientation(RoomUser user, Point point)
        {
            if (GetHighestItemForSquare(point, out Item item))
            {
                user.RotBody = item.Rotation;
                user.RotHead = item.Rotation;
            }
        }

        private void ResetUserMovement(RoomUser user)
        {
            user.GoalX = user.X;
            user.GoalY = user.Y;
            user.SetStep = false;
            user.IsWalking = false;
            user.UpdateNeeded = true;
        }
        #endregion

        #region Map Generation
        public void GenerateMaps(bool checkLines = true)
        {
            ClearMaps();

            if (checkLines && CheckAndExpandMapIfNeeded())
                return;

            InitializeBaseMap();
            ProcessAllItems();
            UpdateUserPositions();

            EnsureDoorAccessible();
        }

        private bool CheckAndExpandMapIfNeeded()
        {
            Item[] items = _room.GetRoomItemHandler().GetFloor.ToArray();
            int maxX = 0, maxY = 0;

            foreach (Item item in items)
            {
                if (item == null) continue;
                if (item.GetX > maxX) maxX = item.GetX;
                if (item.GetY > maxY) maxY = item.GetY;
            }

            if (maxY > Model.MapSizeY - 1 || maxX > Model.MapSizeX - 1)
            {
                Model.SetMapsize(
                    Math.Max(maxX + 7, Model.MapSizeX),
                    Math.Max(maxY + 7, Model.MapSizeY));
                GenerateMaps(false);
                return true;
            }

            return false;
        }

        private void ClearMaps()
        {
            int sizeX = Model.MapSizeX;
            int sizeY = Model.MapSizeY;

            GameMap = new byte[sizeX, sizeY];
            mUserOnMap = new byte[sizeX, sizeY];
            EffectMap = new byte[sizeX, sizeY];
            mSquareTaking = new byte[sizeX, sizeY];
            _itemHeightmap = new double[sizeX, sizeY];
        }

        private void InitializeBaseMap()
        {
            for (int y = 0; y < Model.MapSizeY; y++)
            {
                for (int x = 0; x < Model.MapSizeX; x++)
                {
                    SetDefaultValue(x, y);
                }
            }
        }

        private void ProcessAllItems()
        {
            Item[] items = _room.GetRoomItemHandler().GetFloor.ToArray();
            foreach (var item in items.Where(i => i != null))
            {
                AddItemToMap(item, true, true);
            }
        }

        private void UpdateUserPositions()
        {
            if (_room.RoomBlockingEnabled) return;

            foreach (var user in _room.GetRoomUserManager().GetUserList().Where(u => u != null))
            {
                UpdateUserMapPosition(user);
            }
        }

        private void UpdateUserMapPosition(RoomUser user)
        {
            if (!ValidTile(user.X, user.Y)) return;

            user.SqState = GameMap[user.X, user.Y];
            GameMap[user.X, user.Y] = 0;
            mUserOnMap[user.X, user.Y] = 1;
        }

        private void EnsureDoorAccessible()
        {
            try
            {
                if (ValidTile(Model.DoorX, Model.DoorY))
                    GameMap[Model.DoorX, Model.DoorY] = 3;
            }
            catch { /* Ignorar errores de índice */ }
        }

        private void SetDefaultValue(int x, int y)
        {
            if (!ValidTile(x, y)) return;

            GameMap[x, y] = 0;
            EffectMap[x, y] = 0;
            _itemHeightmap[x, y] = 0.0;

            if (x == Model.DoorX && y == Model.DoorY)
            {
                GameMap[x, y] = 3;
            }
            else if (Model.SqState[x, y] == SquareState.OPEN)
            {
                GameMap[x, y] = 1;
            }
            else if (Model.SqState[x, y] == SquareState.SEAT)
            {
                GameMap[x, y] = 2;
            }
        }
        #endregion

        #region Item Management
        public void AddToMap(Item item) => AddItemToMap(item, true, true);

        public void UpdateMapForItem(Item item)
        {
            RemoveFromMap(item, false);
            AddToMap(item);
        }

        public bool AddItemToMap(Item Item, bool handleGameItem = true, bool NewItem = true)
        {
            if (Item == null) return false;

            if (handleGameItem)
                HandleGameItemRegistration(Item);

            if (Item.GetBaseItem().Type != 's')
                return true;

            foreach (Point coord in Item.GetCoords)
            {
                AddCoordinatedItem(Item, coord);
            }

            if (!CheckMapBounds(Item))
                return false;

            return ConstructMapForAllCoordinates(Item);
        }

        public bool AddItemToMap(Item Item, bool NewItem = true)
        {
            return AddItemToMap(Item, true, NewItem);
        }

        private bool ConstructMapForAllCoordinates(Item item)
        {
            bool success = true;
            foreach (var coord in item.GetCoords)
            {
                if (!ConstructMapForItem(item, coord))
                    success = false;
            }
            return success;
        }

        private bool CheckMapBounds(Item item)
        {
            bool needsRegeneration = false;

            if (item.GetX > Model.MapSizeX - 1)
            {
                Model.AddX();
                needsRegeneration = true;
            }

            if (item.GetY > Model.MapSizeY - 1)
            {
                Model.AddY();
                needsRegeneration = true;
            }

            if (needsRegeneration)
            {
                GenerateMaps(false);
                return false;
            }

            return true;
        }

        private void HandleGameItemRegistration(Item item)
        {
            var interaction = item.GetBaseItem().InteractionType;

            AddSpecialItems(item);

            var teamMap = new Dictionary<InteractionType, TEAM>
            {
                [InteractionType.FOOTBALL_GOAL_RED] = TEAM.RED,
                [InteractionType.footballcounterred] = TEAM.RED,
                [InteractionType.banzaiscorered] = TEAM.RED,
                [InteractionType.banzaigatered] = TEAM.RED,
                [InteractionType.freezeredcounter] = TEAM.RED,
                [InteractionType.FREEZE_RED_GATE] = TEAM.RED,
                [InteractionType.FOOTBALL_GOAL_GREEN] = TEAM.GREEN,
                [InteractionType.footballcountergreen] = TEAM.GREEN,
                [InteractionType.banzaiscoregreen] = TEAM.GREEN,
                [InteractionType.banzaigategreen] = TEAM.GREEN,
                [InteractionType.freezegreencounter] = TEAM.GREEN,
                [InteractionType.FREEZE_GREEN_GATE] = TEAM.GREEN,
                [InteractionType.FOOTBALL_GOAL_BLUE] = TEAM.BLUE,
                [InteractionType.footballcounterblue] = TEAM.BLUE,
                [InteractionType.banzaiscoreblue] = TEAM.BLUE,
                [InteractionType.banzaigateblue] = TEAM.BLUE,
                [InteractionType.freezebluecounter] = TEAM.BLUE,
                [InteractionType.FREEZE_BLUE_GATE] = TEAM.BLUE,
                [InteractionType.FOOTBALL_GOAL_YELLOW] = TEAM.YELLOW,
                [InteractionType.footballcounteryellow] = TEAM.YELLOW,
                [InteractionType.banzaiscoreyellow] = TEAM.YELLOW,
                [InteractionType.banzaigateyellow] = TEAM.YELLOW,
                [InteractionType.freezeyellowcounter] = TEAM.YELLOW,
                [InteractionType.FREEZE_YELLOW_GATE] = TEAM.YELLOW,
            };

            if (teamMap.TryGetValue(interaction, out TEAM team))
            {
                if (!_room.GetRoomItemHandler().GetFloor.Contains(item))
                    _room.GetGameManager().AddFurnitureToTeam(item, team);
            }
            else if (interaction == InteractionType.freezeexit)
            {
                _room.GetFreeze().AddExitTile(item);
            }
            else if (interaction == InteractionType.ROLLER)
            {
                if (!_room.GetRoomItemHandler().GetRollers().Contains(item))
                    _room.GetRoomItemHandler().TryAddRoller(item.Id, item);
            }
        }

        private void AddSpecialItems(Item item)
        {
            switch (item.GetBaseItem().InteractionType)
            {
                case InteractionType.FOOTBALL_GATE:
                    _room.GetSoccer().RegisterGate(item);
                    InitializeGateFigure(item);
                    break;

                case InteractionType.banzaifloor:
                    _room.GetBanzai().AddTile(item, item.Id);
                    break;

                case InteractionType.banzaipyramid:
                    _room.GetGameItemHandler().AddPyramid(item, item.Id);
                    break;

                case InteractionType.banzaitele:
                    _room.GetGameItemHandler().AddTeleport(item, item.Id);
                    item.ExtraData = "";
                    break;

                case InteractionType.banzaipuck:
                    _room.GetBanzai().AddPuck(item);
                    break;

                case InteractionType.FOOTBALL:
                    _room.GetSoccer().AddBall(item);
                    break;

                case InteractionType.FREEZE_TILE_BLOCK:
                    _room.GetFreeze().AddFreezeBlock(item);
                    break;

                case InteractionType.FREEZE_TILE:
                    _room.GetFreeze().AddFreezeTile(item);
                    break;

                case InteractionType.freezeexit:
                    _room.GetFreeze().AddExitTile(item);
                    break;
            }
        }

        private void InitializeGateFigure(Item gate)
        {
            if (string.IsNullOrEmpty(gate.ExtraData))
            {
                gate.Gender = "M";
                gate.Figure = GetDefaultFigureForTeam(gate.team);
            }
            else
            {
                var parts = gate.ExtraData.Split(':');
                if (parts.Length >= 2)
                {
                    gate.Gender = parts[0];
                    gate.Figure = parts[1];
                }
            }
        }

        private string GetDefaultFigureForTeam(TEAM team)
        {
            return team switch
            {
                TEAM.YELLOW => "lg-275-93.hr-115-61.hd-207-14.ch-265-93.sh-305-62",
                TEAM.RED => "lg-275-96.hr-115-61.hd-180-3.ch-265-96.sh-305-62",
                TEAM.GREEN => "lg-275-102.hr-115-61.hd-180-3.ch-265-102.sh-305-62",
                TEAM.BLUE => "lg-275-108.hr-115-61.hd-180-3.ch-265-108.sh-305-62",
                _ => string.Empty
            };
        }

        private bool ConstructMapForItem(Item item, Point coord)
        {
            try
            {
                if (!ValidTile(coord.X, coord.Y))
                    return false;

                if (Model.SqState[coord.X, coord.Y] == SquareState.BLOCKED)
                    Model.OpenSquare(coord.X, coord.Y, item.GetZ);

                if (_itemHeightmap[coord.X, coord.Y] <= item.TotalHeight)
                {
                    _itemHeightmap[coord.X, coord.Y] = item.TotalHeight -
                        _dynamicModel.SqFloorHeight[item.GetX, item.GetY];

                    UpdateEffectMap(item, coord);
                    UpdateGameMap(item, coord);
                }

                if (item.GetBaseItem().InteractionType == InteractionType.BED ||
                    item.GetBaseItem().InteractionType == InteractionType.TENT_SMALL)
                {
                    GameMap[coord.X, coord.Y] = 3;
                }

                return true;
            }
            catch (Exception ex)
            {
                Logging.HandleException(ex, "Room.SqAbsoluteHeight");
                return false;
            }
        }

        private void UpdateEffectMap(Item item, Point coord)
        {
            EffectMap[coord.X, coord.Y] = 0;

            switch (item.GetBaseItem().InteractionType)
            {
                case InteractionType.POOL:
                    EffectMap[coord.X, coord.Y] = 1;
                    break;
                case InteractionType.NORMAL_SKATES:
                    EffectMap[coord.X, coord.Y] = 2;
                    break;
                case InteractionType.ICE_SKATES:
                    EffectMap[coord.X, coord.Y] = 3;
                    break;
                case InteractionType.lowpool:
                    EffectMap[coord.X, coord.Y] = 4;
                    break;
                case InteractionType.haloweenpool:
                    EffectMap[coord.X, coord.Y] = 5;
                    break;
            }
        }

        private void UpdateGameMap(Item item, Point coord)
        {
            var baseItem = item.GetBaseItem();

            if (baseItem.Walkable)
            {
                if (GameMap[coord.X, coord.Y] != 3)
                    GameMap[coord.X, coord.Y] = 1;
            }
            else if (IsOpenGate(item))
            {
                if (GameMap[coord.X, coord.Y] != 3)
                    GameMap[coord.X, coord.Y] = 1;
            }
            else if (baseItem.IsSeat || baseItem.InteractionType == InteractionType.BED ||
                     baseItem.InteractionType == InteractionType.TENT_SMALL)
            {
                GameMap[coord.X, coord.Y] = 3;
            }
            else
            {
                if (GameMap[coord.X, coord.Y] != 3)
                    GameMap[coord.X, coord.Y] = 0;
            }
        }

        private bool IsOpenGate(Item item)
        {
            return item.GetZ <= Model.SqFloorHeight[item.GetX, item.GetY] + 0.1 &&
                   item.GetBaseItem().InteractionType == InteractionType.GATE &&
                   item.ExtraData == "1";
        }

        public bool RemoveFromMap(Item item, bool handleGameItem)
        {
            if (item == null) return false;

            if (handleGameItem)
                RemoveSpecialItem(item);

            bool isRemoved = false;
            foreach (Point coord in item.GetCoords)
            {
                if (RemoveCoordinatedItem(item, coord))
                    isRemoved = true;
            }

            var affectedItems = new ConcurrentDictionary<Point, List<Item>>();
            foreach (Point tile in item.GetCoords)
            {
                Point point = new Point(tile.X, tile.Y);
                if (_coordinatedItems.ContainsKey(point))
                {
                    List<int> ids = (List<int>)_coordinatedItems[point];
                    List<Item> items = GetItemsFromIds(ids);
                    affectedItems.TryAdd(tile, items);
                }

                SetDefaultValue(tile.X, tile.Y);
            }

            foreach (Point coord in affectedItems.Keys)
            {
                if (!affectedItems.ContainsKey(coord))
                    continue;

                List<Item> subItems = (List<Item>)affectedItems[coord];
                foreach (Item subItem in subItems)
                {
                    ConstructMapForItem(subItem, coord);
                }
            }

            return isRemoved;
        }

        public bool RemoveFromMap(Item item) => RemoveFromMap(item, true);

        private void RemoveSpecialItem(Item item)
        {
            switch (item.GetBaseItem().InteractionType)
            {
                case InteractionType.FOOTBALL_GATE:
                    _room.GetSoccer().UnRegisterGate(item);
                    break;
                case InteractionType.banzaifloor:
                    _room.GetBanzai().RemoveTile(item.Id);
                    break;
                case InteractionType.banzaipuck:
                    _room.GetBanzai().RemovePuck(item.Id);
                    break;
                case InteractionType.banzaipyramid:
                    _room.GetGameItemHandler().RemovePyramid(item.Id);
                    break;
                case InteractionType.banzaitele:
                    _room.GetGameItemHandler().RemoveTeleport(item.Id);
                    break;
                case InteractionType.FOOTBALL:
                    _room.GetSoccer().RemoveBall(item.Id);
                    break;
                case InteractionType.FREEZE_TILE:
                    _room.GetFreeze().RemoveFreezeTile(item.Id);
                    break;
                case InteractionType.FREEZE_TILE_BLOCK:
                    _room.GetFreeze().RemoveFreezeBlock(item.Id);
                    break;
                case InteractionType.freezeexit:
                    _room.GetFreeze().RemoveExitTile(item.Id);
                    break;
            }
        }
        #endregion

        #region Coordinated Items Management
        public void AddCoordinatedItem(Item item, Point coord)
        {
            _coordinatedItems.AddOrUpdate(coord,
                new List<int> { item.Id },
                (key, existingList) =>
                {
                    if (!existingList.Contains(item.Id))
                        existingList.Add(item.Id);
                    return existingList;
                });
        }

        public List<Item> GetCoordinatedItems(Point coord)
        {
            var point = new Point(coord.X, coord.Y);
            return _coordinatedItems.TryGetValue(point, out var itemIds)
                ? GetItemsFromIds(itemIds)
                : new List<Item>();
        }

        public bool RemoveCoordinatedItem(Item item, Point coord)
        {
            Point point = new Point(coord.X, coord.Y);
            if (!_coordinatedItems.TryGetValue(point, out var itemIds))
                return false;

            bool removed = itemIds.Remove(item.Id);

            if (itemIds.Count == 0)
                _coordinatedItems.TryRemove(point, out _);
            else
                _coordinatedItems[point] = itemIds;

            return removed;
        }

        public List<Item> GetItemsFromIds(List<int> Input)
        {
            if (Input == null || Input.Count == 0)
                return new List<Item>();

            List<Item> items = new List<Item>();

            try
            {
                foreach (int id in Input.Distinct())
                {
                    Item item = _room.GetRoomItemHandler().GetItem(id);
                    if (item != null && !items.Contains(item))
                        items.Add(item);
                }
            }
            catch (Exception e)
            {
                Logging.LogCriticalException("Error in GetItemsFromIds void: " + e);
            }

            return items;
        }
        #endregion

        #region Tile and Movement Validation
        public bool ValidTile(int X, int Y)
        {
            return X >= 0 && Y >= 0 && X < Model.MapSizeX && Y < Model.MapSizeY;
        }

        public bool CanWalk(int X, int Y, bool Override = false)
        {
            if (!ValidTile(X, Y))
                return false;

            return Override || mUserOnMap[X, Y] == 0;
        }

        public bool SquareHasUsers(int X, int Y)
        {
            if (!ValidTile(X, Y) || mUserOnMap[X, Y] == 0)
                return false;

            return MapGotUser(new Point(X, Y));
        }

        public bool SquareHasUsers(int X, int Y, bool CheckingInvisible = false, bool IsInvisible = false)
        {
            return MapGotUser(new Point(X, Y), CheckingInvisible, IsInvisible);
        }

        public bool ItemCanBePlacedHere(int x, int y)
        {
            if (_dynamicModel.MapSizeX - 1 < x || _dynamicModel.MapSizeY - 1 < y ||
                (x == _dynamicModel.DoorX && y == _dynamicModel.DoorY))
                return false;

            return GameMap[x, y] == 1;
        }

        public bool SquareIsOpen(int x, int y, bool pOverride)
        {
            if ((_dynamicModel.MapSizeX - 1) < x || (_dynamicModel.MapSizeY - 1) < y)
                return false;

            return CanWalk(GameMap[x, y], pOverride);
        }

        public bool ItemCanMove(Item Item, Point MoveTo)
        {
            List<ThreeDCoord> Points = Gamemap.GetAffectedTiles(
                Item.GetBaseItem().Length,
                Item.GetBaseItem().Width,
                MoveTo.X, MoveTo.Y,
                Item.Rotation).Values.ToList();

            if (Points == null || Points.Count == 0)
                return true;

            foreach (ThreeDCoord Coord in Points)
            {
                if (Coord.X >= Model.MapSizeX || Coord.Y >= Model.MapSizeY)
                    return false;

                if (!SquareIsOpen(Coord.X, Coord.Y, false))
                    return false;
            }

            return true;
        }

        public bool IsValidStep(Vector2D From, Vector2D To, bool EndOfPath, bool Override,
            bool Roller = false, bool IsBot = false, bool IsInvisibleUser = false, bool DiagMove = false)
        {
            return IsValidStep(
                new Point(From.X, From.Y),
                new Point(To.X, To.Y),
                EndOfPath, Override, Roller, IsBot, IsInvisibleUser, DiagMove);
        }

        public bool IsValidStep(Point From, Point To, bool EndOfPath, bool Override,
            bool Roller = false, bool IsBot = false, bool IsInvisibleUser = false, bool DiagMove = false)
        {
            if (!ValidTile(To.X, To.Y))
                return false;

            if (Override)
                return true;

            if (!IsBot && !_room.RoomBlockingEnabled && SquareHasUsers(To.X, To.Y, true, IsInvisibleUser))
                return false;

            List<Item> Items = GetAllRoomItemForSquare(To.X, To.Y);
            if (Items.Count > 0 && HasSpecialItemsBlockingMovement(Items, To, EndOfPath))
                return false;

            if (!IsTileWalkable(GameMap[To.X, To.Y], EndOfPath))
                return false;

            if (!Roller && GetHeightDifference(From, To) > 1.5)
                return false;

            if (DiagMove && !IsValidDiagonalMove(From, To))
                return false;

            return true;
        }

        public bool IsValidStep2(RoomUser User, Vector2D From, Vector2D To, bool EndOfPath, bool Override)
        {
            return IsValidStep2(User, new Point(From.X, From.Y), new Point(To.X, To.Y), EndOfPath, Override);
        }

        public bool IsValidStep2(RoomUser User, Point From, Point To, bool EndOfPath, bool Override)
        {
            if (User == null || !ValidTile(To.X, To.Y))
                return false;

            if (Override)
                return true;

            List<Item> Items = GetAllRoomItemForSquare(To.X, To.Y);
            if (Items.Count > 0 && Items.Any(x => x?.GetBaseItem().InteractionType == InteractionType.GUILD_GATE))
            {
                Item gate = Items.FirstOrDefault(x => x?.GetBaseItem().InteractionType == InteractionType.GUILD_GATE);
                if (gate != null)
                    return HandleGroupGateAccess(User, gate);
            }

            bool isChair = false;
            double highestZ = -1;
            foreach (Item item in Items)
            {
                if (item == null) continue;
                if (item.GetZ > highestZ)
                {
                    highestZ = item.GetZ;
                    isChair = item.GetBaseItem().IsSeat;
                }
            }

            byte tileState = GameMap[To.X, To.Y];
            if ((tileState == 3 && !EndOfPath && !isChair) || tileState == 0 || (tileState == 2 && !EndOfPath))
            {
                if (User.Path?.Count > 0)
                    User.Path.Clear();
                User.PathRecalcNeeded = true;
                return false;
            }

            double heightDiff = SqAbsoluteHeight(To.X, To.Y) - SqAbsoluteHeight(From.X, From.Y);
            if (heightDiff > 1.5 && !User.RidingHorse)
                return false;

            RoomUser otherUser = _room.GetRoomUserManager().GetUserForSquare(To.X, To.Y);
            if (otherUser != null && !otherUser.IsWalking && EndOfPath)
                return false;

            return true;
        }

        private bool HandleGroupGateAccess(RoomUser user, Item gate)
        {
            if (user.IsBot)
            {
                OpenGate(gate);
                return true;
            }

            Group group = gate.GroupId < 1000
                ? GroupManager.GetJob(gate.GroupId)
                : GroupManager.GetGang(gate.GroupId);

            if (group == null || user.GetClient()?.GetHabbo() == null)
                return false;

            if (gate.GroupId < 1000)
            {
                GroupRank rank = GroupManager.GetJobRank(group.Id, 1);
                if (rank?.HasCommand("arrest") == true && user.GetClient().GetRoleplay()?.PoliceTrial == true)
                {
                    OpenGate(gate);
                    return true;
                }
            }

            bool hasAccess = group.IsMember(user.GetClient().GetHabbo().Id) &&
                            user.GetClient().GetRoleplay()?.IsWorking == true ||
                            user.GetClient().GetHabbo().GetPermissions().HasRight("corporation_rights") ||
                            GroupManager.HasJobCommand(user.GetClient(), "guide") &&
                            user.GetClient().GetRoleplay()?.IsWorking == true;

            if (hasAccess)
            {
                OpenGate(gate);
                return true;
            }

            if (user.Path?.Count > 0)
                user.Path.Clear();
            user.PathRecalcNeeded = false;
            return false;
        }

        private void OpenGate(Item gate)
        {
            gate.ExtraData = "1";
            gate.UpdateState(false, true);
            gate.RequestUpdate(4, true);
        }

        private bool HasSpecialItemsBlockingMovement(List<Item> items, Point to, bool endOfPath)
        {
            if (items.Any(i => i?.GetBaseItem().InteractionType == InteractionType.GUILD_GATE ||
                              i?.GetBaseItem().InteractionType == InteractionType.SLIDING_DOORS))
                return true;

            var bed = items.FirstOrDefault(i => i?.GetBaseItem().IsBed() == true);
            if (bed != null)
            {
                Point square;
                List<Point> bedTiles = bed.GetBedTiles(new Point(to.X, to.Y), out square);
                if (bedTiles.Any(p => SquareHasUsers(p.X, p.Y)))
                    return true;

                if (!endOfPath)
                    return true;
            }

            return false;
        }

        private bool IsTileWalkable(byte tileState, bool endOfPath)
        {
            if (tileState == 0) return false;
            if (tileState == 2 && !endOfPath) return false;
            if (tileState == 3 && !endOfPath) return false;
            return true;
        }

        private double GetHeightDifference(Point from, Point to)
        {
            return SqAbsoluteHeight(to.X, to.Y) - SqAbsoluteHeight(from.X, from.Y);
        }

        private bool IsValidDiagonalMove(Point from, Point to)
        {
            int dx = to.X - from.X;
            int dy = to.Y - from.Y;

            return (dx, dy) switch
            {
                (-1, -1) => GameMap[to.X + 1, to.Y] == 1 || GameMap[to.X, to.Y + 1] == 1,
                (1, -1) => GameMap[to.X - 1, to.Y] == 1 || GameMap[to.X, to.Y + 1] == 1,
                (1, 1) => GameMap[to.X - 1, to.Y] == 1 || GameMap[to.X, to.Y - 1] == 1,
                (-1, 1) => GameMap[to.X + 1, to.Y] == 1 || GameMap[to.X, to.Y - 1] == 1,
                _ => true
            };
        }

        public static bool CanWalk(byte pState, bool pOverride)
        {
            return pOverride || pState == 1 || pState == 3;
        }
        #endregion

        #region Height and Item Retrieval
        public double SqAbsoluteHeight(int X, int Y)
        {
            Point point = new Point(X, Y);
            if (_coordinatedItems.TryGetValue(point, out var itemIds))
            {
                List<Item> items = GetItemsFromIds(itemIds);
                return SqAbsoluteHeight(X, Y, items);
            }

            return _dynamicModel.SqFloorHeight[X, Y];
        }

        public double SqAbsoluteHeight(int X, int Y, List<Item> ItemsOnSquare)
        {
            try
            {
                bool deduct = false;
                double highestStack = 0;
                double deductable = 0.0;

                if (ItemsOnSquare != null && ItemsOnSquare.Count > 0)
                {
                    foreach (Item item in ItemsOnSquare)
                    {
                        if (item == null) continue;

                        if (item.TotalHeight > highestStack)
                        {
                            if (item.GetBaseItem().IsSeat ||
                                item.GetBaseItem().InteractionType == InteractionType.BED ||
                                item.GetBaseItem().InteractionType == InteractionType.TENT_SMALL)
                            {
                                deduct = true;
                                deductable = item.GetBaseItem().Height;
                            }
                            else
                            {
                                deduct = false;
                            }
                            highestStack = item.TotalHeight;
                        }
                    }
                }

                double floorHeight = Model.SqFloorHeight[X, Y];
                double stackHeight = highestStack - floorHeight;

                if (deduct)
                    stackHeight -= deductable;

                if (stackHeight < 0)
                    stackHeight = 0;

                return floorHeight + stackHeight;
            }
            catch (Exception e)
            {
                Logging.HandleException(e, "Room.SqAbsoluteHeight");
                return 0;
            }
        }

        public bool GetHighestItemForSquare(Point Square, out Item Item)
        {
            Item = null;
            List<Item> items = GetAllRoomItemForSquare(Square.X, Square.Y);

            if (items == null || items.Count == 0)
                return false;

            double highestZ = -1;
            foreach (Item uItem in items)
            {
                if (uItem == null) continue;
                if (uItem.TotalHeight > highestZ)
                {
                    highestZ = uItem.TotalHeight;
                    Item = uItem;
                }
            }

            return Item != null;
        }

        public double GetHeightForSquare(Point Coord)
        {
            if (GetHighestItemForSquare(Coord, out Item rItem) && rItem != null)
                return rItem.TotalHeight;

            return 0.0;
        }

        public List<Item> GetAllRoomItemForSquare(int pX, int pY)
        {
            Point coord = new Point(pX, pY);
            return _coordinatedItems.TryGetValue(coord, out var itemIds)
                ? GetItemsFromIds(itemIds)
                : new List<Item>();
        }

        public List<Item> GetRoomItemForSquare(int pX, int pY, double minZ)
        {
            var itemsToReturn = new List<Item>();
            var coord = new Point(pX, pY);

            if (_coordinatedItems.TryGetValue(coord, out var itemIds))
            {
                var items = GetItemsFromIds(itemIds);
                foreach (Item item in items)
                {
                    if (item.GetZ > minZ && item.GetX == pX && item.GetY == pY)
                        itemsToReturn.Add(item);
                }
            }

            return itemsToReturn;
        }

        public List<Item> GetRoomItemForSquare(int pX, int pY)
        {
            var itemsToReturn = new List<Item>();
            var coord = new Point(pX, pY);

            if (_coordinatedItems.TryGetValue(coord, out var itemIds))
            {
                var items = GetItemsFromIds(itemIds);
                foreach (Item item in items)
                {
                    if (item.Coordinate.X == coord.X && item.Coordinate.Y == coord.Y)
                        itemsToReturn.Add(item);
                }
            }

            return itemsToReturn;
        }
        #endregion

        #region Utility Methods
        public Point GetRandomWalkableSquare()
        {
            try
            {
                var walkableSquares = GetWalkableSquares()
                    .Where(p => p.X != StaticModel.DoorX && p.Y != StaticModel.DoorY)
                    .ToList();

                if (walkableSquares == null || walkableSquares.Count == 0)
                    return new Point(0, 0);

                int randomIndex = PolarEnvironment.GetRandomNumber(0, walkableSquares.Count - 1);

                // Validación adicional del índice
                if (randomIndex < 0 || randomIndex >= walkableSquares.Count)
                    return new Point(0, 0);

                return walkableSquares[randomIndex];
            }
            catch (Exception ex)
            {
                // Log del error si es necesario
                return new Point(0, 0);
            }
        }

        private IEnumerable<Point> GetWalkableSquares()
        {
            for (int y = 0; y < GameMap.GetLength(1); y++)
            {
                for (int x = 0; x < GameMap.GetLength(0); x++)
                {
                    if (GameMap[x, y] == 1)
                        yield return new Point(x, y);
                }
            }
        }

        public Point GetRandomWalkableSquare(int x, int y)
        {
            int rx = PolarEnvironment.GetRandomNumber(x - 5, x + 5);
            int ry = PolarEnvironment.GetRandomNumber(y - 5, y + 5);

            if (Model.DoorX == rx || Model.DoorY == ry || !CanWalk(rx, ry))
                return new Point(x, y);

            return new Point(rx, ry);
        }

        public bool IsInMap(int X, int Y)
        {
            var walkableSquares = GetWalkableSquares()
                .Where(p => p.X != StaticModel.DoorX && p.Y != StaticModel.DoorY)
                .ToList();

            return walkableSquares.Contains(new Point(X, Y));
        }

        public static Dictionary<int, ThreeDCoord> GetAffectedTiles(int Length, int Width, int PosX, int PosY, int Rotation)
        {
            int x = 0;
            var PointList = new Dictionary<int, ThreeDCoord>();

            if (Length > 1)
            {
                if (Rotation == 0 || Rotation == 4)
                {
                    for (int i = 1; i < Length; i++)
                    {
                        if (!PointList.Values.Contains(new ThreeDCoord(PosX, PosY + i, i)))
                            PointList.Add(x++, new ThreeDCoord(PosX, PosY + i, i));

                        for (int j = 1; j < Width; j++)
                        {
                            if (!PointList.Values.Contains(new ThreeDCoord(PosX + j, PosY + i, Math.Max(i, j))))
                                PointList.Add(x++, new ThreeDCoord(PosX + j, PosY + i, Math.Max(i, j)));
                        }
                    }
                }
                else if (Rotation == 2 || Rotation == 6)
                {
                    for (int i = 1; i < Length; i++)
                    {
                        if (!PointList.Values.Contains(new ThreeDCoord(PosX + i, PosY, i)))
                            PointList.Add(x++, new ThreeDCoord(PosX + i, PosY, i));

                        for (int j = 1; j < Width; j++)
                        {
                            if (!PointList.Values.Contains(new ThreeDCoord(PosX + i, PosY + j, Math.Max(i, j))))
                                PointList.Add(x++, new ThreeDCoord(PosX + i, PosY + j, Math.Max(i, j)));
                        }
                    }
                }
            }

            if (Width > 1)
            {
                if (Rotation == 0 || Rotation == 4)
                {
                    for (int i = 1; i < Width; i++)
                    {
                        if (!PointList.Values.Contains(new ThreeDCoord(PosX + i, PosY, i)))
                            PointList.Add(x++, new ThreeDCoord(PosX + i, PosY, i));

                        for (int j = 1; j < Length; j++)
                        {
                            if (!PointList.Values.Contains(new ThreeDCoord(PosX + i, PosY + j, Math.Max(i, j))))
                                PointList.Add(x++, new ThreeDCoord(PosX + i, PosY + j, Math.Max(i, j)));
                        }
                    }
                }
                else if (Rotation == 2 || Rotation == 6)
                {
                    for (int i = 1; i < Width; i++)
                    {
                        if (!PointList.Values.Contains(new ThreeDCoord(PosX, PosY + i, i)))
                            PointList.Add(x++, new ThreeDCoord(PosX, PosY + i, i));

                        for (int j = 1; j < Length; j++)
                        {
                            if (!PointList.Values.Contains(new ThreeDCoord(PosX + j, PosY + i, Math.Max(i, j))))
                                PointList.Add(x++, new ThreeDCoord(PosX + j, PosY + i, Math.Max(i, j)));
                        }
                    }
                }
            }

            if (!PointList.Values.Contains(new ThreeDCoord(PosX, PosY, 0)))
                PointList.Add(PointList.Count + 1, new ThreeDCoord(PosX, PosY, 0));

            return PointList;
        }

        public Point GetChaseMovement(Item Item)
        {
            int distance = 99;
            Point coord = new Point(0, 0);
            int iX = Item.GetX;
            int iY = Item.GetY;
            bool isHorizontal = false;

            foreach (RoomUser user in _room.GetRoomUserManager().GetRoomUsers())
            {
                if (user.X == Item.GetX || Item.GetY == user.Y)
                {
                    if (user.X == Item.GetX)
                    {
                        int diff = Math.Abs(user.Y - Item.GetY);
                        if (diff < distance)
                        {
                            distance = diff;
                            coord = user.Coordinate;
                            isHorizontal = false;
                        }
                    }
                    else if (user.Y == Item.GetY)
                    {
                        int diff = Math.Abs(user.X - Item.GetX);
                        if (diff < distance)
                        {
                            distance = diff;
                            coord = user.Coordinate;
                            isHorizontal = true;
                        }
                    }
                }
            }

            if (distance > 5)
                return Item.GetSides().OrderBy(x => Guid.NewGuid()).FirstOrDefault();

            if (isHorizontal && distance < 99)
            {
                return new Point(iX > coord.X ? iX - 1 : iX + 1, iY);
            }
            else if (!isHorizontal && distance < 99)
            {
                return new Point(iX, iY > coord.Y ? iY - 1 : iY + 1);
            }

            return Item.Coordinate;
        }

        public RoomUser SquareHasUserNear(int X, int Y, int Distance = 0)
        {
            if (SquareHasUsers(X - 1, Y))
                return _room.GetRoomUserManager().GetUserForSquare(X - 1, Y);
            if (SquareHasUsers(X + 1, Y))
                return _room.GetRoomUserManager().GetUserForSquare(X + 1, Y);
            if (SquareHasUsers(X, Y - 1))
                return _room.GetRoomUserManager().GetUserForSquare(X, Y - 1);
            if (SquareHasUsers(X, Y + 1))
                return _room.GetRoomUserManager().GetUserForSquare(X, Y + 1);

            return null;
        }

        public static bool TilesTouching(Point p1, Point p2)
        {
            return TilesTouching(p1.X, p1.Y, p2.X, p2.Y);
        }

        public static bool TilesTouching(int X1, int Y1, int X2, int Y2)
        {
            return Math.Abs(X1 - X2) <= 1 && Math.Abs(Y1 - Y2) <= 1;
        }

        public static int TileDistance(int X1, int Y1, int X2, int Y2)
        {
            return Math.Abs(X1 - X2) + Math.Abs(Y1 - Y2);
        }

        public byte GetFloorStatus(Point coord)
        {
            if (coord.X > GameMap.GetUpperBound(0) || coord.Y > GameMap.GetUpperBound(1))
                return 1;

            return GameMap[coord.X, coord.Y];
        }

        public void SetFloorStatus(int X, int Y, byte Status)
        {
            if (ValidTile(X, Y))
                GameMap[X, Y] = Status;
        }

        public double GetHeightForSquareFromData(Point coord)
        {
            if (coord.X > _dynamicModel.SqFloorHeight.GetUpperBound(0) ||
                coord.Y > _dynamicModel.SqFloorHeight.GetUpperBound(1))
                return 1;
            return _dynamicModel.SqFloorHeight[coord.X, coord.Y];
        }

        public bool CanRollItemHere(int x, int y, HabboHotel.GameClients.GameClient Session)
        {
            if (!ValidTile(x, y) || Model.SqState[x, y] == SquareState.BLOCKED)
                return false;

            return _room.CheckTerrain(Session, x, y);
        }

        public bool CanRollItemHere(int x, int y)
        {
            return ValidTile(x, y) && Model.SqState[x, y] != SquareState.BLOCKED;
        }
        #endregion

        #region Properties
        public DynamicRoomModel Model => _dynamicModel;
        public RoomModel StaticModel => _staticModel;
        #endregion

        #region IDisposable Implementation
        public void Dispose()
        {
            _userMap?.Clear();
            _coordinatedItems?.Clear();
            _dynamicModel?.Destroy();

            GameMap = null;
            EffectMap = null;
            mUserOnMap = null;
            mSquareTaking = null;
            _itemHeightmap = null;
            _room = null;
        }
        #endregion
    }
}