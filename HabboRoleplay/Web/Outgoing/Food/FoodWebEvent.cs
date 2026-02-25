using System;
using System.Linq;
using System.Text;
using System.Threading.Tasks;

using Fleck;
using Polar.HabboHotel.Items;
using Polar.HabboHotel.GameClients;
using Polar.HabboHotel.Rooms;
using System.IO;
using Polar.HabboRoleplay.Misc;
using Polar.HabboRoleplay.Food;
using System.Data;
using Polar.HabboRoleplay.Bots.Manager;

namespace Polar.HabboHotel.Roleplay.Web.Outgoing.Misc
{
    /// <summary>
    /// WeaponsWebEvent class.
    /// </summary>
    class FoodWebEvent : IWebEvent
    {
        /// <summary>
        /// Executes socket data.
        /// </summary>
        /// <param name="Client"></param>
        /// <param name="Data"></param>
        /// <param name="Socket"></param>
        public void Execute(GameClient Client, string Data, IWebSocketConnection Socket)
        {
            // Verificaciones iniciales
            if (Client == null || Socket == null)
                return;

            if (!PolarEnvironment.GetGame().GetWebEventManager().SocketReady(Client, true) ||
                !PolarEnvironment.GetGame().GetWebEventManager().SocketReady(Socket))
                return;

            var BotUser = RoleplayBotManager.GetDeployedBotById(10);

            // Verificar BotUser
            if (BotUser == null)
            {
                Socket.Send("compose_shop_restaurant|error|Bot no disponible");
                return;
            }

            if (string.IsNullOrEmpty(Data))
                return;

            string Action = (Data.Contains(',') ? Data.Split(',')[0] : Data);

            switch (Action)
            {
                #region Open Food
                case "open":
                    {
                        #region Conditions & Vars
                        string[] ReceivedData = Data.Split(',');

                        // Verificar que hay suficientes elementos
                        if (ReceivedData.Length < 2)
                            return;

                        var ServableFoods = FoodManager.GetServableBotItems(ReceivedData[1]);

                        // Verificar que hay alimentos
                        if (ServableFoods == null || !ServableFoods.Any())
                        {
                            Socket.Send("compose_shop_restaurant|error|No hay alimentos disponibles");
                            return;
                        }
                        #endregion

                        #region HTML
                        string html = "";
                        ItemData Datax = null;

                        foreach (var Food in ServableFoods.OrderBy(x => x.Cost))
                        {
                            // Verificar Food no sea null
                            if (Food == null)
                                continue;

                            // Verificar que GetItem funciona
                            if (!PolarEnvironment.GetGame().GetItemManager().GetItem(Food.ItemId, out Datax))
                                continue;

                            // Verificar Datax no sea null
                            if (Datax == null)
                                continue;

                            html += "<div data-balloon=\"" + Food.Name + "\" data-balloon-pos=\"right\" id=\"comprarcomida\" class=\"p-1 w-1/2\" style=\"box-sizing: border-box; width: 11%;\">";
                            html += "<div class=\"box p-4 flex items-center cursor-pointer-r hover:bg-dark-3\" style=\"padding: 0.5rem !important;\" id=\"menu-comida\" comida=\"" + Food.Name + "\">";
                            html += "<div class=\"flex justify-center items-center\" style=\"width: 50px;height: 50px;\">";
                            html += "<img  src=\"" + RoleplayManager.CDNSWF + "/dcr/hof_furni/" + Datax.ItemName + "_icon.png\" class=\"mr-2 flex-none\" style=\"left: 15%; position: relative;\">";
                            html += "</div>";
                            html += "</div>";
                            html += "</div>";
                        }
                        #endregion

                        string SendData = "";
                        SendData += html;
                        Socket.Send("compose_shop_restaurant|open|" + SendData);
                        break;
                    }
                #endregion

                #region Buy Food
                case "shop":
                    {
                        if (!RoleplayManager.GenerateRoom(Client.GetRoomUser().RoomId, out Room Room))
                            return;

                        string[] ReceivedData = Data.Split(',');

                        // Verificar que hay suficientes elementos
                        if (ReceivedData.Length < 2)
                            return;

                        string DesiredFood = ReceivedData[1];

                        RoomUser RoomUser = Room.GetRoomUserManager().GetRoomUserByHabbo(Client.GetHabbo().Id);

                        // Verificar RoomUser
                        if (RoomUser == null)
                            return;

                        // Verificar que RoomUser.OnChat no sea null
                        if (RoomUser.OnChat != null)
                            RoomUser.OnChat(RoomUser.LastBubble, "servir " + DesiredFood, false, string.Empty);

                        // Verificar Client.GetRoleplay()
                        if (Client.GetRoleplay() != null)
                        {
                            Client.GetRoleplay().ClearWebSocketDialogue();
                            Client.GetRoleplay().RefreshStatDialogue();
                            Client.GetRoleplay().UpdateInteractingUserDialogues();
                            Client.GetRoleplay().RefreshStatDialogue();
                        }
                        break;
                    }
                #endregion

                #region Open Food y Close Food
                case "openfood":
                case "close":
                    {
                        // Estos casos son simples, pero aún así verificar Socket
                        if (Socket != null)
                            Socket.Send("compose_shop_restaurant|" + Action + "|");
                        break;
                    }
                    #endregion
            }
        }
    }
}
