package javafxapplication1.network;

import java.io.IOException;
import java.util.Iterator;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;

public class RYMParser {
    
    private final String artistUrl = "http://rateyourmusic.com/artist/";
    private final String albumUrl = "http://rateyourmusic.com/release/album/";
    private String formed;
    private String disbanded;
    private String members;
    private String memberOf;
    private String related;
    private String notes;
    private String aka;
    private String genres;
    
    public RYMParser() {
    }
    
    public void parseArtistInfo(String artistName) throws IOException{
        String url = artistUrl + validateUrl(artistName);
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(20000).get();
        Element artistTitle = doc.select("div.bubble_header  ").first();
        Element contentTable = doc.select("table.mbgen").first();
        
        int i = 0;
        for (Iterator<Element> ite = contentTable.select("td").iterator(); ite.hasNext();) {
            String item = ite.next().text();
            System.out.println(i++ + " " + item);
        }
    }

    public void init() throws IOException {
        String url = "http://rateyourmusic.com/artist/jimi_hendrix";
        Document doc = Jsoup.connect(url)
                .userAgent("Mozilla/5.0 (X11; Linux x86_64) AppleWebKit/535.21 (KHTML, like Gecko) Chrome/19.0.1042.0 Safari/535.21")
                .timeout(20000).get();
        String title = doc.title();
        Element artistTitle = doc.select("div.bubble_header  ").first();
        Element contentTable = doc.select("table.mbgen").first();
        
        int i = 0;
        for (Iterator<Element> ite = contentTable.select("td").iterator(); ite.hasNext();) {
            String item = ite.next().text();
            System.out.println(i++ + " " + item);
        }
        
        /*Element bioformed = contentTable.getElementById("bioformed");
        System.out.println(title);
        System.out.println(artistTitle.text());*/
    }

    public String getArtistUrl() {
        return artistUrl;
    }

    public String getAlbumUrl() {
        return albumUrl;
    }

    public String getFormed() {
        return formed;
    }

    public void setFormed(String formed) {
        this.formed = formed;
    }

    public String getDisbanded() {
        return disbanded;
    }

    public void setDisbanded(String disbanded) {
        this.disbanded = disbanded;
    }

    public String getMembers() {
        return members;
    }

    public void setMembers(String members) {
        this.members = members;
    }

    public String getMemberOf() {
        return memberOf;
    }

    public void setMemberOf(String memberOf) {
        this.memberOf = memberOf;
    }

    public String getRelated() {
        return related;
    }

    public void setRelated(String related) {
        this.related = related;
    }

    public String getNotes() {
        return notes;
    }

    public void setNotes(String notes) {
        this.notes = notes;
    }

    public String getAka() {
        return aka;
    }

    public void setAka(String aka) {
        this.aka = aka;
    }

    public String getGenres() {
        return genres;
    }

    public void setGenres(String genres) {
        this.genres = genres;
    }
    
    private String validateUrl(String url){
        StringBuilder sb = new StringBuilder();
        char[] charArr = url.toLowerCase().toCharArray();
        for (int i = 0; i < charArr.length; i++) {
            if (charArr[i]=='á') {sb.append("a");}
            else if (charArr[i]=='à') {sb.append("a");}
            else if (charArr[i]=='â') {sb.append("a");}
            else if (charArr[i]=='ã') {sb.append("a");}
            else if (charArr[i]=='ä') {sb.append("a");}
            else if (charArr[i]=='å') {sb.append("a");}
            else if (charArr[i]=='ā') {sb.append("a");}
            else if (charArr[i]=='ă') {sb.append("a");}
            else if (charArr[i]=='ą') {sb.append("a");}
            else if (charArr[i]=='ȁ') {sb.append("a");}
            else if (charArr[i]=='ȃ') {sb.append("a");}
            else if (charArr[i]=='ß') {sb.append("b");}
            else if (charArr[i]=='ć') {sb.append("c");}
            else if (charArr[i]=='ĉ') {sb.append("c");}
            else if (charArr[i]=='ċ') {sb.append("c");}
            else if (charArr[i]=='č') {sb.append("c");}
            else if (charArr[i]=='ç') {sb.append("c");}
            else if (charArr[i]=='è') {sb.append("e");}
            else if (charArr[i]=='é') {sb.append("e");}
            else if (charArr[i]=='ê') {sb.append("e");}
            else if (charArr[i]=='ë') {sb.append("e");}
            else if (charArr[i]=='ē') {sb.append("e");}
            else if (charArr[i]=='ĕ') {sb.append("e");}
            else if (charArr[i]=='ė') {sb.append("e");}
            else if (charArr[i]=='ę') {sb.append("e");}
            else if (charArr[i]=='ě') {sb.append("e");}
            else if (charArr[i]=='ȅ') {sb.append("e");}
            else if (charArr[i]=='ȇ') {sb.append("e");}
            else if (charArr[i]=='ĝ') {sb.append("g");}
            else if (charArr[i]=='ğ') {sb.append("g");}
            else if (charArr[i]=='ġ') {sb.append("g");}
            else if (charArr[i]=='ģ') {sb.append("g");}
            else if (charArr[i]=='ĥ') {sb.append("h");}
            else if (charArr[i]=='ħ') {sb.append("h");}
            else if (charArr[i]=='ì') {sb.append("i");}
            else if (charArr[i]=='í') {sb.append("i");}
            else if (charArr[i]=='î') {sb.append("i");}
            else if (charArr[i]=='ï') {sb.append("i");}
            else if (charArr[i]=='ĩ') {sb.append("i");}
            else if (charArr[i]=='ȉ') {sb.append("i");}
            else if (charArr[i]=='ȉ') {sb.append("i");}
            else if (charArr[i]=='ȋ') {sb.append("i");}
            else if (charArr[i]=='ĭ') {sb.append("i");}
            else if (charArr[i]=='į') {sb.append("i");}
            else if (charArr[i]=='ı') {sb.append("i");}
            else if (charArr[i]=='ĵ') {sb.append("j");}
            else if (charArr[i]=='ķ') {sb.append("k");}
            else if (charArr[i]=='ĺ') {sb.append("l");}
            else if (charArr[i]=='ļ') {sb.append("l");}
            else if (charArr[i]=='ľ') {sb.append("l");}
            else if (charArr[i]=='ŀ') {sb.append("l");}
            else if (charArr[i]=='ł') {sb.append("l");}
            else if (charArr[i]=='ð') {sb.append("d");}
            else if (charArr[i]=='ď') {sb.append("d");}
            else if (charArr[i]=='đ') {sb.append("d");}
            else if (charArr[i]=='þ') {sb.append("d");}
            else if (charArr[i]=='æ') {sb.append("ae");}
            else if (charArr[i]=='ñ') {sb.append("n");}
            else if (charArr[i]=='ń') {sb.append("n");}
            else if (charArr[i]=='ņ') {sb.append("n");}
            else if (charArr[i]=='ň') {sb.append("n");}
            else if (charArr[i]=='ŉ') {sb.append("n");}
            else if (charArr[i]=='ò') {sb.append("o");}
            else if (charArr[i]=='ó') {sb.append("o");}
            else if (charArr[i]=='ô') {sb.append("o");}
            else if (charArr[i]=='õ') {sb.append("o");}
            else if (charArr[i]=='ö') {sb.append("o");}
            else if (charArr[i]=='ō') {sb.append("o");}
            else if (charArr[i]=='ŏ') {sb.append("o");}
            else if (charArr[i]=='ő') {sb.append("o");}
            else if (charArr[i]=='ǒ') {sb.append("o");}
            else if (charArr[i]=='ǫ') {sb.append("o");}
            else if (charArr[i]=='ǭ') {sb.append("o");}
            else if (charArr[i]=='ȍ') {sb.append("o");}
            else if (charArr[i]=='ȏ') {sb.append("o");}
            else if (charArr[i]=='ø') {sb.append("o");}
            else if (charArr[i]=='ǿ') {sb.append("o");}
            else if (charArr[i]=='ŕ') {sb.append("r");}
            else if (charArr[i]=='ŗ') {sb.append("r");}
            else if (charArr[i]=='ř') {sb.append("r");}
            else if (charArr[i]=='ȑ') {sb.append("r");}
            else if (charArr[i]=='ȓ') {sb.append("r");}
            else if (charArr[i]=='ś') {sb.append("s");}
            else if (charArr[i]=='ŝ') {sb.append("s");}
            else if (charArr[i]=='ş') {sb.append("s");}
            else if (charArr[i]=='š') {sb.append("s");}
            else if (charArr[i]=='ţ') {sb.append("t");}
            else if (charArr[i]=='ť') {sb.append("t");}
            else if (charArr[i]=='ŧ') {sb.append("t");}
            else if (charArr[i]=='ù') {sb.append("u");}
            else if (charArr[i]=='ú') {sb.append("u");}
            else if (charArr[i]=='û') {sb.append("u");}
            else if (charArr[i]=='ü') {sb.append("u");}
            else if (charArr[i]=='ũ') {sb.append("u");}
            else if (charArr[i]=='ȕ') {sb.append("u");}
            else if (charArr[i]=='ȗ') {sb.append("u");}
            else if (charArr[i]=='ū') {sb.append("u");}
            else if (charArr[i]=='ŭ') {sb.append("u");}
            else if (charArr[i]=='ů') {sb.append("u");}
            else if (charArr[i]=='ų') {sb.append("u");}
            else if (charArr[i]=='ý') {sb.append("y");}
            else if (charArr[i]=='ÿ') {sb.append("y");}
            else if (charArr[i]=='ź') {sb.append("y");}
            else if (charArr[i]=='ż') {sb.append("y");}
            else if (charArr[i]=='ž') {sb.append("y");}
            else if (charArr[i]=='½') {sb.append("_");}
            else if (charArr[i]=='¬') {sb.append("_");}
            else if (charArr[i]=='#') {sb.append("_");}
            else if (charArr[i]=='{') {sb.append("_");}
            else if (charArr[i]=='}') {sb.append("_");}
            else if (charArr[i]==']') {sb.append("_");}
            else if (charArr[i]=='[') {sb.append("_");}
            else if (charArr[i]==')') {sb.append("_");}
            else if (charArr[i]=='(') {sb.append("_");}
            else if (charArr[i]=='\'') {sb.append("_");}
            else if (charArr[i]=='-') {sb.append("_");}
            else if (charArr[i]=='+') {sb.append("_");}
            else if (charArr[i]=='=') {sb.append("_");}
            else if (charArr[i]=='\\') {sb.append("_");}
            else if (charArr[i]=='/') {sb.append("_");}
            else if (charArr[i]=='?') {sb.append("_");}
            else if (charArr[i]=='!') {sb.append("_");}
            else if (charArr[i]==';') {sb.append("_");}
            else if (charArr[i]==':') {sb.append("_");}
            else if (charArr[i]==',') {sb.append("_");}
            else if (charArr[i]=='.') {sb.append("_");}
            else if (charArr[i]=='^') {sb.append("_");}
            else if (charArr[i]=='%') {sb.append("_");}
            else if (charArr[i]=='$') {sb.append("_");}
            else if (charArr[i]=='№') {sb.append("_");}
            else if (charArr[i]=='<') {sb.append("_");}
            else if (charArr[i]=='>') {sb.append("_");}
            else if (charArr[i]=='~') {sb.append("_");}
            else if (charArr[i]=='`') {sb.append("_");}
            else if (charArr[i]=='*') {sb.append("_");}
            else if (charArr[i]==' ') {sb.append("_");}
            else if (charArr[i]=='"') {sb.append("");}
            else if (charArr[i]=='&') {sb.append("and");}
            else sb.append(url.charAt(i));
        }
        return sb.toString().toLowerCase();
    }
}
