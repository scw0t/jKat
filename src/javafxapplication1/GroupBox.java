package javafxapplication1;

import javafx.geometry.Orientation;
import javafx.scene.control.Label;
import javafx.scene.control.Separator;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class GroupBox extends HBox {

    private int id;
    private Image img;
    private Image logo;
    private String artist;
    private String origin;
    private String album;
    private String year;
    private String type;
    private HBox hbox;
    private VBox vbox;
    private Label rootLabel;
    private Label artistNameLabel, artistAlbumsLabel, artistOriginLabel;
    private Label albumNameLabel, albumYearLabel, albumTypeLabel, albumArtistLabel;
    private TreeItem parentTreeItem;
    private TreeView parentTreeView;
    private boolean deleteMenuItemFired;
    
    // 1 - artists
    // 2 - albums
    private int flag;

    public GroupBox() {
        super();
    }
    
    public GroupBox(String string) {
        super();
        getChildren().addAll(new Label(string));
    }

    public void initArtistBox(Image img, String artist, String origin, int id){
        this.id = id;
        this.img = img;
        this.artist = artist;
        this.origin = origin;
        artistNameLabel = new Label(this.artist);
        artistAlbumsLabel = new Label("Альбомов: ");
        artistOriginLabel = new Label("[" + this.origin + "]");
        
        hbox = new HBox();
        vbox = new VBox();
        hbox.setSpacing(5);
        vbox.setSpacing(5);
        
        hbox.getChildren().addAll(artistNameLabel, artistOriginLabel);
        vbox.getChildren().addAll(hbox, artistAlbumsLabel);
        this.getChildren().addAll(new ImageView(img), new Separator(Orientation.VERTICAL), vbox);
    }
    
    public void initAlbumBox(Image img, String album, String year, String type , String artist, int id){
        this.id = id;
        this.img = img;
        this.album = album;
        this.year = year;
        this.type = type;
        this.artist = artist;
        albumNameLabel = new Label(this.album);
        albumYearLabel = new Label("(" + this.year + ")");
        albumTypeLabel = new Label("[" + this.type + "]");
        albumArtistLabel = new Label(this.artist);
        albumArtistLabel.setStyle("-fx-font-family: serif; -fx-font-size: 12;");
        
        hbox = new HBox();
        vbox = new VBox();
        hbox.setSpacing(5);
        vbox.setSpacing(12);
        hbox.getChildren().addAll(albumNameLabel, albumYearLabel, albumTypeLabel);
        vbox.getChildren().addAll(hbox, albumArtistLabel);
        this.getChildren().addAll(new ImageView(img), new Separator(Orientation.VERTICAL), vbox);
    }
    
    public void initLabelBox(String rootName, int id){
        this.id = id;
        rootLabel = new Label(rootName);
        this.getChildren().addAll(rootLabel);
    }
    
    public String getArtistNameLabel() {
        if (artistNameLabel != null) {
            return artistNameLabel.getText();
        } else {
            return "";
        }
    }

    public void setArtistNameLabel(String artistLabel) {
        artistNameLabel.setText(album);
    }

    public String getOriginNameLabel() {
        if (artistOriginLabel != null) {
            return artistOriginLabel.getText();
        } else {
            return "";
        }
    }

    public void setOriginNameLabel(String origin) {
        artistOriginLabel.setText(origin);
    }

    public String getRootLabel() {
        if (rootLabel != null) {
            return rootLabel.getText();
        } else {
            return "";
        }
    }

    public void setRootLabel(String rootName) {
        rootLabel.setText(rootName);
    }

    public String getAlbumNameLabel() {
        if (albumNameLabel != null) {
            return albumNameLabel.getText();
        } else {
            return "";
        }
    }

    public void setAlbumNameLabel(String album) {
        albumNameLabel.setText(album);
    }

    public String getAlbumYearLabel() {
        if (albumYearLabel != null) {
            return albumYearLabel.getText();
        } else {
            return "";
        }
    }

    public void setAlbumYearLabel(String year) {
        albumYearLabel.setText(year);
    }

    public String getAlbumTypeLabel() {
        if (albumTypeLabel != null) {
            return albumTypeLabel.getText();
        } else {
            return "";
        }
    }

    public void setAlbumTypeLabel(String type) {
        albumTypeLabel.setText(type);
    }

    public String getAlbumArtistLabel() {
        if (albumArtistLabel != null) {
            return albumArtistLabel.getText();
        } else {
            return "";
        }
    }

    public void setAlbumArtistLabel(String artist) {
        albumArtistLabel.setText(artist);
    }

    public int getMyId() {
        return id;
    }

    public void setMyId(int id) {
        this.id = id;
    }

    public boolean deleteMenuItemFired(){
        return deleteMenuItemFired;
    }

    public TreeItem getParentTreeItem() {
        return parentTreeItem;
    }

    public void setParentTreeItem(TreeItem parentTreeItem) {
        this.parentTreeItem = parentTreeItem;
    }

    public TreeView getParentTreeView() {
        return parentTreeView;
    }

    public void setParentTreeView(TreeView parentTreeView) {
        this.parentTreeView = parentTreeView;
    }

    public int getFlag() {
        return flag;
    }

    public void setFlag(int flag) {
        this.flag = flag;
    }
}
