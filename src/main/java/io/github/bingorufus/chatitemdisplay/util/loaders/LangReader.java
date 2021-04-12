package io.github.bingorufus.chatitemdisplay.util.loaders;

import com.google.gson.JsonObject;
import com.google.gson.JsonParser;
import com.google.gson.stream.JsonReader;
import io.github.bingorufus.chatitemdisplay.ChatItemDisplay;

import java.io.InputStream;
import java.io.InputStreamReader;

public class LangReader {
    public JsonObject readLang(String langkey) {
        langkey = langkey.trim();
        InputStream res = ChatItemDisplay.getInstance().getResource("langs/{key}.json".replace("{key}", langkey));
        if (res == null) throw new NullPointerException("Language Key {key} does not exist".replace("{key}", langkey));
        return (JsonObject) new JsonParser().parse(new JsonReader(new InputStreamReader(res)));
    }
}
