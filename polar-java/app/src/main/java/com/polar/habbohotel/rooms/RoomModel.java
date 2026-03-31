package com.polar.habbohotel.rooms;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoomModel {
    private int doorOrientation;
    private int doorX;
    private int doorY;
    private double doorZ;
    private String heightmap;
    private int mapSizeX;
    private int mapSizeY;
    private short[][] sqFloorHeight;
    private byte[][] sqSeatRot;
    private SquareState[][] sqState;
    private boolean gotPublicPool;
    private byte[][] mRoomModelfx;
    private int wallHeight;

    public RoomModel(String id, int doorX, int doorY, double doorZ, int doorOrientation, String heightmap, int wallHeight, String poolmap) {
        this.doorX = doorX;
        this.doorY = doorY;
        this.doorZ = doorZ;
        this.doorOrientation = doorOrientation;
        this.wallHeight = wallHeight;
        this.heightmap = heightmap.toLowerCase();
        this.gotPublicPool = poolmap != null && !poolmap.isEmpty();

        String[] tmpHeightmap = heightmap.split("\r");

        this.mapSizeX = tmpHeightmap[0].length();
        this.mapSizeY = tmpHeightmap.length;

        sqState = new SquareState[mapSizeX][mapSizeY];
        sqFloorHeight = new short[mapSizeX][mapSizeY];
        sqSeatRot = new byte[mapSizeX][mapSizeY];
        if (gotPublicPool) {
            mRoomModelfx = new byte[mapSizeX][mapSizeY];
        }

        for (int y = 0; y < mapSizeY; y++) {
            String line = tmpHeightmap[y].replace("\r", "").replace("\n", "");
            for (int x = 0; x < line.length() && x < mapSizeX; x++) {
                char square = line.charAt(x);
                if (square == 'x') {
                    sqState[x][y] = SquareState.BLOCKED;
                } else {
                    sqState[x][y] = SquareState.OPEN;
                    sqFloorHeight[x][y] = parseHeight(square);
                }
            }
        }
    }

    public static short parseHeight(char input) {
        if (input >= '0' && input <= '9') return (short) (input - '0');
        if (input >= 'a' && input <= 'z') return (short) (10 + (input - 'a'));
        return 0;
    }

    public void destroy() {
        heightmap = null;
        sqState = null;
        sqFloorHeight = null;
        sqSeatRot = null;
    }
}
