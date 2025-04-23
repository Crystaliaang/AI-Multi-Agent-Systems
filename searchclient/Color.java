package searchclient;

import java.util.Locale;

public enum Color
{
    Blue,
    Red,
    Cyan,
    Purple,
    Green,
    Orange,
    Pink,
    Grey,
    Lightblue,
    Brown;

    public static Color fromString(String s)
    {
        switch (s.toLowerCase(Locale.ROOT))
        {
            case "blue":
                return Blue;
            case "red":
                return Red;
            case "cyan":
                return Cyan;
            case "purple":
                return Purple;
            case "green":
                return Green;
            case "orange":
                return Orange;
            case "pink":
                return Pink;
            case "grey":
                return Grey;
            case "lightblue":
                return Lightblue;
            case "brown":
                return Brown;
            default:
                return null;
        }
    }
    public static String toString(Color c){
        switch (c)
        {
            case Blue:
                return "blue";
            case Red:
                return "red";
            case Cyan:
                return "cyan";
            case Purple:
                return "purple";
            case Green:
                return "green";
            case Orange:
                return "orange";
            case Pink:
                return "pink";
            case Grey:
                return "grey";
            case Lightblue:
                return "lightblue";
            case Brown:
                return "brown";
            default:
                return null;
        }
    }
}
