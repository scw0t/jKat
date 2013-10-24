package javafxapplication1;

import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.geometry.Insets;
import javafx.scene.control.Label;
import javafx.scene.control.Tab;
import javafx.scene.control.TabPane;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;

public class MainTab {

    private Tab tab = new Tab("Главная");
    private TabPane tabPane;
    private SqlWorker sqlWorker;
    private String artistsSum, albumsSum, labelsSum, songsSum, genresSum;
    private Label artistLabel, albumsLabel, labelsLabel, songsLabel, genresLabel;
    private HBox hbox12;

    public MainTab(TabPane tabPane, SqlWorker sqlWorker) {
        this.tabPane = tabPane;
        this.sqlWorker = sqlWorker;
    }

    public void init() throws SQLException {
        tab.setClosable(false);
        this.tabPane.getTabs().add(tab);
        addFormatBoxes();
    }

    private void addFormatBoxes() {
        // Верхняя часть - общая статистика (2 HBox'a )
        VBox vbox1 = new VBox();
        HBox hbox11 = new HBox();
        vbox1.setPadding(new Insets(5));
        hbox11.setPadding(new Insets(5));
        hbox11.setStyle("-fx-background-color: linear-gradient(#e2ecfe, #99bcfd);");

        Label artistCountLabel = new Label("Кол-во исполнителей: ");
        Label albumsCountLabel = new Label("Кол-во альбомов: ");
        Label labelsCountLabel = new Label("Кол-во лейблов: ");
        Label songsCountLabel = new Label("Кол-во песен: ");
        Label genresCountLabel = new Label("Кол-во жанров: ");
        VBox labelsVBox = new VBox(); // названия граф для общей статистики
        labelsVBox.getChildren().addAll(artistCountLabel, albumsCountLabel,
                labelsCountLabel, songsCountLabel, genresCountLabel); // добавляем лейблы

        artistLabel = new Label();
        albumsLabel = new Label();
        labelsLabel = new Label();
        genresLabel = new Label();
        songsLabel = new Label();

        VBox commonStatVBox = new VBox();

        commonStatVBox.getChildren().addAll(artistLabel, albumsLabel, labelsLabel, songsLabel, genresLabel); // добавляем соотв. численные значения

        hbox11.getChildren().addAll(labelsVBox, commonStatVBox); // ставим HBox'ы по горизонтали

        // Ниже

        hbox12 = new HBox();
        hbox12.setStyle("-fx-background-color: linear-gradient(#e2ef00, #99bcfd);");
        hbox12.setPadding(new Insets(5));

        vbox1.getChildren().addAll(hbox11, hbox12);
        tab.setContent(vbox1);
        
        try {
            update();
        } catch (SQLException ex) {
            System.out.println("##### Ошибка обновления таба со статистикой");
        }
    }

    public void update() throws SQLException {
        ResultSet rs = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS artistSum FROM artists;");
        rs.next();
        artistsSum = rs.getString("artistSum");
        artistLabel.setText(artistsSum);

        rs = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS albumsSum FROM albums;");
        rs.next();
        albumsSum = rs.getString("albumsSum");
        albumsLabel.setText(albumsSum);

        rs = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS labelsSum FROM labels;");
        rs.next();
        labelsSum = rs.getString("labelsSum");
        labelsLabel.setText(labelsSum);

        rs = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS songsSum FROM songs;");
        rs.next();
        songsSum = rs.getString("songsSum");
        songsLabel.setText(songsSum);

        rs = sqlWorker.getStat().executeQuery("SELECT COUNT(*) AS genresSum FROM genres;");
        rs.next();
        genresSum = rs.getString("genresSum");
        genresLabel.setText(genresSum);
        rs.close();
    }

    public Tab getTab() {
        return tab;
    }

    public void setTab(Tab tab) {
        this.tab = tab;
    }
}
