package me.dylancurzon.dontdie.tile;

import me.dylancurzon.dontdie.util.ByteBuf;
import me.dylancurzon.dontdie.util.Vector2i;

import java.io.*;
import java.nio.ByteBuffer;
import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

public class Level {

    private final Map<Vector2i, TileType> tileMap;

    public Level() {
        this.tileMap = new HashMap<>();
    }

    public Level(final Map<Vector2i, TileType> tileMap) {
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
    public static Level fromFile(final File file) {
        BufferedInputStream in = null;
        try {
            try {
                in = new BufferedInputStream(new FileInputStream(file));
            } catch (final FileNotFoundException e) {
                throw new RuntimeException("The File that was passed doesn't exist: ", e);
            }
            final ByteBuffer buffer;
            try {
                final int available = in.available();
                final byte[] buf = new byte[available];
                in.read(buf, 0, available);
                buffer = ByteBuffer.wrap(buf);
            } catch (final IOException e) {
                throw new RuntimeException("Exception occurred when loading bytes from file: ", e);
            }
            final ByteBuf buf = new ByteBuf(buffer);

            final Map<Vector2i, TileType> tileMap = new HashMap<>();
            final int numTiles = buf.readInt();

            for (int i = 0; i < numTiles; i++) {
                final int x = buf.readInt();
                final int y = buf.readInt();
                final int id = buf.readByte();
                final Optional<TileType> type = TileType.forId(id);
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
    public Optional<TileType> setTile(final Vector2i position, final TileType newTile) {
        if (newTile == null) {
            final TileType tile = this.tileMap.remove(position);
            return Optional.ofNullable(tile);
        }

        final TileType tile = this.tileMap.put(position, newTile);
        return Optional.ofNullable(tile);
    }

        public Optional<TileType> getTile(final Vector2i position) {
        if (this.tileMap.containsKey(position)) {
            return Optional.of(this.tileMap.get(position));
        }
        return Optional.empty();
    }

    public void save(final File file) {
        if (!file.exists()) {
            file.getParentFile().mkdirs();
            try {
                file.createNewFile();
            } catch (final IOException e) {
                throw new RuntimeException("Failed to create the file: ", e);
            }
        }
        BufferedOutputStream out = null;
        try {
            try {
                out = new BufferedOutputStream(new FileOutputStream(file));
            } catch (final FileNotFoundException e) {
                throw new RuntimeException("The file we just created doesn't exist... wait, what? - ", e);
            }
            // numTiles (4) + numTiles * [x (4) + y (4) + id (1)]
            final byte[] rawBuf = new byte[4 + this.tileMap.size() * 9];
            final ByteBuf buf = new ByteBuf(ByteBuffer.wrap(rawBuf));

            buf.writeInt(this.tileMap.size());
            this.tileMap.forEach((pos, tile) -> {
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
