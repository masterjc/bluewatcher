package com.bluewatcher.util;

import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.util.Properties;

import android.content.Context;
import android.content.res.AssetManager;

/**
 * @version $Revision$
 */
public class Transliterator {
	private static final String TRANSLITERATION_PROPERTIES = "transliteration.properties";

	private Properties transliteration;

	public Transliterator(Context context) {
		AssetManager assets = context.getAssets();
		InputStream stream = null;
		try {
			stream = assets.open(TRANSLITERATION_PROPERTIES);
			transliteration = new Properties();
			transliteration.load(new InputStreamReader(stream, "UTF-8"));
			stream.close();
		}
		catch (IOException e) {
			transliteration = null;
		}
	}

	public String translate(String input) {
		if (transliteration == null)
			return input;
		StringBuffer output = new StringBuffer();
		for (int position = 0; position < input.length(); position++) {
			char[] characters = { input.charAt(position) };
			String key = new String(characters);
			if (transliteration.containsKey(key)) {
				String trans = (String) transliteration.get(key);
				if (trans != null && !trans.isEmpty()) {
					if( trans.contains("|") ) {
						String[] more = trans.split("|");
						if (more.length == 2) {
							if (position == 0) {
								output.append(more[0]);
							}
							else {
								output.append(more[1]);
							}
						}
						else {
							output.append(transliteration.get(key));
						}
					}
					else {
						output.append(transliteration.get(key));
					}
				}
			}
			else {
				output.append(characters);
			}
		}
		return output.toString();
	}
}
