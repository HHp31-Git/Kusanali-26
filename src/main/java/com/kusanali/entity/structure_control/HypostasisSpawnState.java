package com.kusanali.entity.structure_control;

import net.minecraft.nbt.NbtCompound;
import net.minecraft.server.MinecraftServer;
import net.minecraft.world.PersistentState;
import net.minecraft.world.PersistentStateManager;
import net.minecraft.world.World;

import java.util.HashMap;
import java.util.Map;
import java.util.Objects;

public class HypostasisSpawnState extends PersistentState {

    private static final String KEY = "hypostasis_spawn_state";
    private final Map<String, Boolean> spawnedStructures = new HashMap<>();

    @Override
    public NbtCompound writeNbt(NbtCompound nbt) {
        NbtCompound structuresTag = new NbtCompound();
        for (Map.Entry<String, Boolean> entry : spawnedStructures.entrySet()) {
            structuresTag.putBoolean(entry.getKey(), entry.getValue());
        }
        nbt.put("spawned_structures", structuresTag);
        return nbt;
    }

    public static HypostasisSpawnState fromNbt(NbtCompound nbt) {
        HypostasisSpawnState state = new HypostasisSpawnState();
        NbtCompound structuresTag = nbt.getCompound("spawned_structures");
        for (String key : structuresTag.getKeys()) {
            state.spawnedStructures.put(key, structuresTag.getBoolean(key));
        }
        return state;
    }

    public boolean hasSpawned(String structureId) {
        return spawnedStructures.getOrDefault(structureId, false);
    }

    public void markSpawned(String structureId) {
        spawnedStructures.put(structureId, true);
        markDirty();
    }

    public static HypostasisSpawnState getServerState(MinecraftServer server) {
        PersistentStateManager manager = Objects.requireNonNull(server.getWorld(World.OVERWORLD))
                .getPersistentStateManager();

        return manager.getOrCreate(
                HypostasisSpawnState::fromNbt,
                HypostasisSpawnState::new,
                KEY
        );
    }
}