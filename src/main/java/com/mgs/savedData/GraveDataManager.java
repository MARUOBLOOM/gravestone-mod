package com.mgs.savedData;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;

public final class GraveDataManager {
    private static final Gson GSON = new GsonBuilder().setPrettyPrinting().setDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX").create();

    // プレイヤーの死亡情報一覧をまとめたJSONを生成するメソッド
    public static void createGraveJsonData(Path savePath, GraveData graveData) throws IOException {
        String json = GSON.toJson(graveData);
        Files.writeString(savePath, json, StandardCharsets.UTF_8, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
    }

    // プレイヤーの死亡情報一覧をまとめたJSONデータを読み込むメソッド
    public static GraveData readGraveJsonData(Path savePath) throws IOException {
        String json = Files.readString(savePath);
        return GSON.fromJson(json, GraveData.class);
    }
}