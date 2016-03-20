package com.example.us.boxes;
import com.badlogic.gdx.Gdx;
import com.badlogic.gdx.graphics.g2d.BitmapFont;
import com.badlogic.gdx.graphics.g2d.freetype.FreeTypeFontGenerator;
import java.util.Locale;

public class FontFactory {
    // Font names
    public static final String ENGLAND = "goodfish rg.ttf";
    public static final String RUSSIAN = "Imperial Web.ttf";

    // Russian cyrillic characters
    //public static final String RUSSIAN_CHARACTERS = "�����Ũ��������������������������" + "��������������������������������" + "1234567890.,:;_?!??\"'+-*/()[]={}";

    // Singleton: unique instance
    private static FontFactory instance;


    private BitmapFont enFont;
    private BitmapFont ruFont;

    /** Private constructor for singleton pattern */
    private FontFactory() { super(); }

    public static synchronized FontFactory getInstance() {
        if (instance == null) {
            instance = new FontFactory();
        }
        return instance;
    }

    public void initialize() {
        // If fonts are already generated, dispose it!
        if (enFont != null) enFont.dispose();
        if (ruFont != null) ruFont.dispose();

        enFont = generateFont(ENGLAND, FreeTypeFontGenerator.DEFAULT_CHARS);
        ruFont = generateFont(RUSSIAN, FreeTypeFontGenerator.DEFAULT_CHARS);
    }

    private BitmapFont generateFont(String fontName, String characters) {

        // Configure font parameters
        FreeTypeFontGenerator.FreeTypeFontParameter parameter = new FreeTypeFontGenerator.FreeTypeFontParameter();
        parameter.characters = characters;
        parameter.size = 24;

        // Generate font
        FreeTypeFontGenerator generator = new FreeTypeFontGenerator( Gdx.files.internal(fontName) );
        BitmapFont font = generator.generateFont(parameter);

        // Dispose resources
        generator.dispose();

        return font;
    }
    public BitmapFont getFont(Locale locale) {
        if      ("es".equals(locale.getLanguage())) return enFont;
        else if ("ru".equals(locale.getLanguage())) return ruFont;
        else throw new IllegalArgumentException("Not supported language");
    }

    public void dispose() {
        enFont.dispose();
        ruFont.dispose();
    }
}
