package javafxapplication1;

import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.scene.Group;
import javafx.scene.Scene;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.scene.input.MouseButton;
import javafx.scene.input.MouseEvent;
import javafx.stage.FileChooser;
import javafx.stage.Stage;
import javafx.stage.StageStyle;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;

public class CustomImageView extends ImageView {

    private int id;
    private SqlWorker sqlWorker;
    private ResultSet resultSet;
    private String userDir = System.getProperty("user.home");
    private String initDir = userDir + "\\" + "jCollector";
    private String imageDir = initDir + "\\" + "Images";
    private String thumbsDir = imageDir + "\\" + "thumbs";
    private String entity;
    private String idAlias;
    private int flag; // 1 - artist, 2 - album, 3 - label
    private String imagePath; 

    public CustomImageView(final int id, int flag, final SqlWorker sqlWorker) {
        super();
        this.id = id;
        this.sqlWorker = sqlWorker;

        if (flag == 1) {
            entity = "artists";
            idAlias = "idartist";
        } else if (flag == 2) {
            entity = "albums";
            idAlias = "idalbum";
        } else if (flag == 3) {
            entity = "labels";
            idAlias = "idlabel";
        }

        setOnMouseClicked(new EventHandler<MouseEvent>() {
            @Override
            public void handle(MouseEvent event) {
                if (event.getButton().equals(MouseButton.PRIMARY)) {
                    if (event.getClickCount() == 2) {
                        File imgPath = null;
                        try {
                            PreparedStatement stmt = sqlWorker.getConnection().
                                    prepareStatement("SELECT img FROM " + entity + " WHERE " + idAlias + " = ?");
                            stmt.setInt(1, id);
                            resultSet = stmt.executeQuery();
                            resultSet.next();
                            imgPath = new File(resultSet.getString("img"));
                            Stage st = new Stage();
                            st.initOwner(CustomImageView.this.getScene().getWindow());
                            st.initStyle(StageStyle.UTILITY);
                            st.setResizable(false);
                            st.setScene(new Scene(new Group(new ImageView(imgPath.toURI().toString()))));
                            st.show();
                        } catch (SQLException e) {
                        }
                    }
                }

                if (event.getButton().equals(MouseButton.SECONDARY)) {
                    try {
                        MenuItem menuItem = new MenuItem("Change image...");
                        menuItem.setOnAction(new EventHandler<ActionEvent>() {
                            @Override
                            public void handle(ActionEvent t) {
                                FileChooser fileChooser = new FileChooser();
                                File file = fileChooser.showOpenDialog(null);
                                if (file != null) {
                                    deletePrevImage();
                                    changeImage(file);
                                    setImage(new Image(file.toURI().toString(), 150, 150, false, true));
                                }

                            }
                        });
                        ContextMenu contextMenu = new ContextMenu();
                        contextMenu.getItems().add(menuItem);
                        contextMenu.show(CustomImageView.this, event.getScreenX(), event.getScreenY());
                    } catch (Exception e) {
                    }
                }
            }
        });
        
        /*addEventHandler(EventType.ROOT, new EventHandler<Event>() {
            @Override
            public void handle(Event t) {
                throw new UnsupportedOperationException("Not supported yet."); //To change body of generated methods, choose Tools | Templates.
            }
        });*/
    }

    private void changeImage(File file) {
        String imgPath = makeImage(file);
        String thumbPath = makeThumb(file);
        updateImagePath(imgPath);
        updateThumbsPath(thumbPath);
    }

    private void deletePrevImage() {
        try {
            PreparedStatement stmt = sqlWorker.getConnection().
                    prepareStatement("SELECT img, thumb FROM " + entity + " WHERE " + idAlias + " = ?");
            stmt.setInt(1, id);
            resultSet = stmt.executeQuery();
            resultSet.next();
            File imagePath = new File(resultSet.getString("img"));
            imagePath.delete();
            imagePath = new File(resultSet.getString("thumb"));
            imagePath.delete();
        } catch (SQLException ex) {
            System.out.println("##### Ошибка удаления фото исполнителя");
            ex.printStackTrace();
        }
    }

    private String makeImage(File imgFile) {
        File f = null;
        String dirName = imgFile.getAbsoluteFile().getParentFile().getName();
        try {
            File imgDir = new File(imageDir + "\\" + dirName.charAt(0));
            if (!imgDir.exists()) {
                imgDir.mkdir();
            }
            f = new File(imgDir, dirName + "_fld" + ".jpg");
            FileUtils.copyFile(imgFile, f);
        } catch (IOException ex) {
            System.out.println("##### Ошибка копирования " + imgFile.getName());
        }
        return f.getAbsolutePath();
    }

    private String makeThumb(File imgFile) {
        File f = null;
        String dirName = imgFile.getAbsoluteFile().getParentFile().getName();
        try {
            File imgDir = new File(thumbsDir + "\\" + dirName.charAt(0));
            if (!imgDir.exists()) {
                imgDir.mkdir();
            }
            f = new File(imgDir, dirName + "_thumb" + ".jpg");
            BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
            img.createGraphics().drawImage(ImageIO.read(imgFile).
                    getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            ImageIO.write(img, "jpg", f);
        } catch (IOException ex) {
            System.out.println("##### Ошибка копирования " + imgFile.getName());
        }
        return f.getAbsolutePath();
    }

    public void updateImagePath(String imgPath) {
        try {
            String query = "UPDATE " + entity + " SET img = ? WHERE " + idAlias + " = ?";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setString(1, imgPath);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }

    public void updateThumbsPath(String imgPath) {
        try {
            String query = "UPDATE " + entity + " SET thumb = ? WHERE " + idAlias + " = ?";
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query);
            stmt.setString(1, imgPath);
            stmt.setInt(2, id);
            stmt.executeUpdate();
        } catch (SQLException e) {
            e.printStackTrace();
        }
    }
}
