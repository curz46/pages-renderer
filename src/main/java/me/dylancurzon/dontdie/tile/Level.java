package me.dylancurzon.dontdie.tile;

import me.dylancurzon.dontdie.util.ByteBuf;
import me.dylancurzon.pages.util.Vector2i;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Level {

    private final Map<Vector2i, TileType> tileMap;

    public static Level generateTestLevel() {
        Level level = new Level();
        for (int x = -250; x < 250; x++) {
            for (int y = -250; y < 250; y++) {
                level.setTile(Vector2i.of(x, y), (x + y) % 2 == 0 ? TileType.STONEBRICKS : TileType.UNDEFINED);
            }
        }
        return level;
    }

    public Level() {
        tileMap = new HashMap<>();
    }

    public Level(Map<Vector2i, TileType> tileMap) {
        this.tileMap = tileMap;
    }

    /**
     * Levels have a binary representation that is slightly different to that of Worlds -- they don't work with
     * chunks.
     *
     * # tiles written
     * integer; number of tiles
     * # foreach tile
     * integer; X position
     * integer; Y position
     * byte; tile type id
     * TODO: tile metadata
     * # end foreach
     * TODO: entities
     *
     * @param file the File to attempt to load the Level from.
     * @return the loaded Level.
     */
    public static Level fromFile(File file) {
        BufferedInputStream in = null;
        try {
            try {
                in = new BufferedInputStream(new FileInputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("The File that was passed doesn't exist: ", e);
            }
            ByteBuffer buffer;
            try {
                int available = in.available();
                byte[] buf = new byte[available];
                in.read(buf, 0, available);
                buffer = ByteBuffer.wrap(buf);
            } catch (IOException e) {
                throw new RuntimeException("Exception occurred when loading bytes from file: ", e);
            }
            ByteBuf buf = new ByteBuf(buffer);

            Map<Vector2i, TileType> tileMap = new HashMap<>();
            int numTiles = buf.readInt();

            for (int i = 0; i < numTiles; i++) {
                int x = buf.readInt();
                int y = buf.readInt();
                int id = buf.readByte();
                Optional<TileType> type = TileType.forId(id);
                if (!type.isPresent()) throw new RuntimeException("Level contains unrecognised TileType id: " + id);
                tileMap.put(Vector2i.of(x, y), type.get());
            }

            return new Level(tileMap);
        } finally {
            // Ensure that the InputStream is closed, regardless of Exceptions.
            if (in != null) {
                try {
                    in.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

    /**
     * Sets a Tile position to the given Tile.
     * @param position the position to set in the Tile map.
     * @param newTile the Tile to set the position to. If this is null, the position is removed from the Tile map.
     * @return the Tile that was previously set at that position, if present.
     */
    public Optional<TileType> setTile(Vector2i position, TileType newTile) {
        if (newTile == null) {
            TileType tile = tileMap.remove(position);
            return Optional.ofNullable(tile);
        }

        TileType tile = tileMap.put(position, newTile);
        return Optional.ofNullable(tile);
    }

    public Optional<TileType> getTile(Vector2i position) {
        if (tileMap.containsKey(position)) {
            return Optional.of(tileMap.get(position));
        }
        return Optional.empty();
    }

    public void save(File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (IOException e) {
                throw new RuntimeException("Failed to create the file: ", e);
            }
        }
        BufferedOutputStream out = null;
        try {
            try {
                out = new BufferedOutputStream(new FileOutputStream(file));
            } catch (FileNotFoundException e) {
                throw new RuntimeException("The file we just created doesn't exist... wait, what? - ", e);
            }
            // numTiles (4) + numTiles * [x (4) + y (4) + id (1)]
            byte[] rawBuf = new byte[4 + tileMap.size() * 9];
            ByteBuf buf = new ByteBuf(ByteBuffer.wrap(rawBuf));

            buf.writeInt(tileMap.size());
            tileMap.forEach((pos, tile) -> {
                buf.writeInt(pos.getX());
                buf.writeInt(pos.getY());
                buf.writeByte((byte) tile.getId());
            });

            out.flush();
        } catch (IOException e) {
            throw new RuntimeException("Failed to write contents to file: ", e);
        } finally {
            if (out != null) {
                try {
                    out.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
    }

}
