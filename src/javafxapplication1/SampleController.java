package javafxapplication1;

//import com.javafx.experiments.scenicview.ScenicView;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.net.URL;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.ResourceBundle;
import java.util.logging.Level;
import java.util.logging.Logger;
import javafx.fxml.FXML;
import javafx.fxml.Initializable;
import javafx.scene.control.Button;
import javafx.scene.control.ListView;
import javafx.scene.control.SplitPane;
import javafx.scene.control.TabPane;
import javafx.scene.control.TreeView;
import javafx.scene.layout.Pane;
import javafx.stage.DirectoryChooser;
import javafxapplication1.network.RYMParser;
import javax.imageio.ImageIO;
import org.apache.commons.io.FileUtils;
import org.apache.commons.io.filefilter.DirectoryFileFilter;
import org.apache.commons.io.filefilter.NotFileFilter;
import org.apache.commons.io.filefilter.TrueFileFilter;
import org.jaudiotagger.audio.exceptions.CannotReadException;
import org.jaudiotagger.audio.exceptions.InvalidAudioFrameException;
import org.jaudiotagger.audio.exceptions.ReadOnlyFileException;
import org.jaudiotagger.tag.TagException;

public class SampleController implements Initializable {

    @FXML
    private Button testButton;
    @FXML
    private TabPane tabPane;
    @FXML
    private ListView<GroupBox> albumsList;
    @FXML
    private ListView<GroupBox> artistList;
    //@FXML
    //private TreeView<String> collectionTreeView;
    @FXML
    private Button openButton;
    @FXML
    private TreeView<GroupBox> tempoTreeView;
    @FXML
    private Pane collectionAnchorPane;
    @FXML
    private SplitPane splitPane;
    @FXML
    private MainTab mainTab;
    private SqlWorker sqlWorker;
    private String userDir = System.getProperty("user.home");
    private String initDir = userDir + "\\" + "jCollector";
    private String imageDir = initDir + "\\" + "Images";
    private String thumbsDir = imageDir + "\\" + "thumbs";
    ArrayList<File> mp3List = new ArrayList<>();
    File[] currM3pList;
    File[] currJpgList;
    File folder;
    ListProducer listProducer;
    private TreeViewWithItems collectionTreeView;
    CollectionListProducer collectionListProducer;
    Progress progress;

    @FXML
    void handleTestButtonAction() throws IOException, SQLException {
        RYMParser conn = new RYMParser();
        conn.parseArtistInfo("Genesis P-Orridge");
        //conn.init();
    }

    @FXML
    void handleOpenButtonAction() throws IOException, SQLException {
        DirectoryChooser dirChooser = new DirectoryChooser();
        File initDir = new File("D:\\mz");
        dirChooser.setInitialDirectory(initDir);
        File selectedDirectory = dirChooser.showDialog(openButton.getScene().getWindow());

        /*File[] fList = selectedDirectory.listFiles();
         String pattern1 = "([^\\.]*)";
         String pattern2 = "(\\d+)$";
         Pattern p1 = Pattern.compile(pattern1, Pattern.CASE_INSENSITIVE);
         Pattern p2 = Pattern.compile(pattern2, Pattern.CASE_INSENSITIVE);

         for (File file : fList) {
         if (file.isFile()) {
         String name = file.getName();
         Matcher m = p1.matcher(name);
         if (m.lookingAt()) {
         Matcher m2 = p2.matcher(m.group(1));
         if (m2.find()) {
         file.renameTo(new File(initDir + "\\" + m2.group(1) + " - " + m.group(1) + ".jpg"));
         }
         }
         }
         }*/

        folder = null;
        currJpgList = null;
        currM3pList = null;

        try {
            newFolderExplorer(selectedDirectory);
            mainTab.update();
            listProducer.init();
            collectionListProducer.init();
        } catch (NullPointerException e) {
            System.out.println("##### Папка не выбрана");
        }
    }

    public void listFiles(String directoryName) {

        File directory = new File(directoryName);

        File[] fList = directory.listFiles();

        for (File file : fList) {
            if (file.isFile()) {
                System.out.println(file.getName());
            }
        }
    }

    @FXML
    void handleCleanButtonAction() throws IOException {
        String query1 = "DELETE FROM Artists;";
        String query2 = "DELETE FROM Albums;";
        String query3 = "DELETE FROM Genres;";
        String query4 = "DELETE FROM Labels WHERE idlabel <> 1;";

        try {
            PreparedStatement stmt = sqlWorker.getConnection().prepareStatement(query1);
            stmt.executeUpdate();
            stmt = sqlWorker.getConnection().prepareStatement(query2);
            stmt.executeUpdate();
            stmt = sqlWorker.getConnection().prepareStatement(query3);
            stmt.executeUpdate();
            stmt = sqlWorker.getConnection().prepareStatement(query4);
            stmt.executeUpdate();
            mainTab.update();
            listProducer.init();
            collectionListProducer.init();
            System.out.println("DB dropped successfully");
        } catch (SQLException ex) {
            System.out.println("##### Не удалось очистить БД");
        }

        FileUtils.deleteDirectory(new File(imageDir));

        File dir = new File(initDir);
        File imgDir = new File(imageDir);
        File tmbDir = new File(thumbsDir);

        if (!dir.exists()) {
            dir.mkdir();
        }

        if (!imgDir.exists()) {
            imgDir.mkdir();
        }

        if (!tmbDir.exists()) {
            tmbDir.mkdir();
        }
    }
    /*
     private void folderExplorer(File dir) throws IOException, SQLException {
     boolean hasCover = false;
     boolean isFolderContainsMP3 = false;
     String size;
     int mp3DirAmount = 0;
     File fileWithCover = null;

     ArrayList<File> releaseList = new ArrayList<File>();
     Release release = new Release(sqlWorker);

     File[] fileList = dir.listFiles();
     mp3DirAmount = mp3DirAmount(fileList);
     for (int i = 0; i < fileList.length; i++) {
     isFolderContainsMP3 = false;
     hasCover = false;
     if (fileList[i].isDirectory()) {
     if (!releaseList.isEmpty()) {
     releaseList.clear();
     }
     System.out.println("------------------");
     System.out.println("subDir: ");
     folderExplorer(fileList[i]);
     } else if (fileList[i].isFile()) {
     if (i == 0) {
     release = new Release(sqlWorker);
     }

     if (fileList[i].getName().contains(".mp3")) {
     isFolderContainsMP3 = true;
     releaseList.add(fileList[i]);
     System.out.println(fileList[i].getCanonicalPath());
     if (i == mp3DirAmount - 1) {
     try {
     release.parseList(releaseList);
     } catch (CannotReadException | TagException | ReadOnlyFileException | InvalidAudioFrameException ex) {
     System.out.println("UNCLASSIFIED ERROR!");
     }
     }
     } else if ((fileList[i].getName().toLowerCase().equals("folder.jpg")
     || fileList[i].getName().toLowerCase().equals("cover.jpg"))
     && (!fileList[i].getParentFile().getName().toLowerCase().equals("scans")
     && !fileList[i].getParentFile().getName().toLowerCase().equals("covers"))) {
     String coverURL = makeAlbumCover(fileList[i]);
     String thumbsURL = makeThumbnails(fileList[i]);
     release.getAlbum().updateCoverPath(coverURL);
     release.getAlbum().updateThumbsPath(thumbsURL);
     hasCover = true;
     }

     if (isFolderContainsMP3 && !hasCover && i == fileList.length - 1) {

     String coverURL = makeAlbumCover(new File("temp.png"));
     String thumbsURL = makeThumbnails(new File("temp.png"));
     release.getAlbum().updateCoverPath(coverURL);
     release.getAlbum().updateThumbsPath(thumbsURL);
     }

     if (!isFolderContainsMP3 && hasCover && i == fileList.length - 1) {
     String coverURL = makeAlbumCover(fileList[i]);
     String thumbsURL = makeThumbnails(fileList[i]);
     release.getAlbum().updateCoverPath(coverURL);
     release.getAlbum().updateThumbsPath(thumbsURL);
     }

     if (isFolderContainsMP3) {
     }
     }
     }
     }*/

    public void newFolderExplorer(File dir) throws IOException, SQLException {
        progress = new Progress(openButton);
        Collection<File> list;
        String[] str = {"mp3"};
        list = FileUtils.listFiles(dir, str, true);

        for (File f : FileUtils.listFilesAndDirs(dir, new NotFileFilter(TrueFileFilter.INSTANCE), DirectoryFileFilter.DIRECTORY)) {
            if (f.isDirectory()) {
                Aggregator aggregator = null;

                currM3pList = f.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File f, String name) {
                        return name.toLowerCase().endsWith(".mp3");
                    }
                });

                currJpgList = f.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File f, String name) {
                        return name.toLowerCase().endsWith(".jpg");
                    }
                });

                File[] folderList = f.listFiles(new FilenameFilter() {
                    @Override
                    public boolean accept(File f, String name) {
                        if ((name.toLowerCase().equals("folder.jpg")
                                || name.toLowerCase().equals("cover.jpg"))
                                && (!f.getName().toLowerCase().equals("scans")
                                && !f.getName().toLowerCase().equals("covers"))) {
                            return true;
                        } else {
                            return false;
                        }
                    }
                });

                if (currJpgList.length == 1) {
                    folder = currJpgList[0];
                }

                // просто папка
                if (currM3pList.length == 0 && currJpgList.length == 0) {
                    folder = null;
                    System.out.println("Текущая папка: " + f.getName());
                    //folderExplorerTest(f, depth);
                }

                // есть и мп3 и обложка
                if (currM3pList.length != 0 && folder != null) {
                    //release = new Release(sqlWorker, currM3pList, folder);
                    aggregator = new Aggregator(sqlWorker, currM3pList, folder);

                    System.out.println("* Список файлов: ");
                    for (int i = 0; i < currM3pList.length; i++) {
                        System.out.println(currM3pList[i].getName());
                    }

                    System.out.println("+ Обложка: ");
                    System.out.println(folder);
                }

                // есть мп3, но нет обложки
                if (currM3pList.length != 0 && folder == null) {
                    if (folder != null) {
                        //release = new Release(sqlWorker, currM3pList, folder);
                        aggregator = new Aggregator(sqlWorker, currM3pList, folder);
                    } else {
                        //release = new Release(sqlWorker, currM3pList);
                        aggregator = new Aggregator(sqlWorker, currM3pList);

                        System.out.println("* Список файлов: ");
                        for (int i = 0; i < currM3pList.length; i++) {
                            System.out.println(currM3pList[i].getName());
                        }

                        System.out.println("Обложки нет!");
                    }
                }

                // папка с обложками
                if (currM3pList.length == 0 && currJpgList.length != 0) {
                    if (folder != null) {
                        System.out.println("Обложка (в папке выше): " + folder.getAbsolutePath());
                    } else {
                        System.out.println("Содержимое папки " + f.getName() + ":");
                        for (int i = 0; i < currJpgList.length; i++) {
                            System.out.println(currJpgList[i].getName());
                        }
                    }
                }

                if (aggregator != null) {
                    try {
                        aggregator.process();
                        if (folder != null) {
                            String imgURL = makeAlbumImage(folder);
                            String thumbsURL = makeThumbnails(folder);
                            aggregator.getAlbum().updateImagePath(imgURL);
                            aggregator.getAlbum().updateThumbsPath(thumbsURL);
                        } else {
                            File defaultImg = new File("temp.png");
                            String imgURL = makeAlbumImage(defaultImg);
                            String thumbsURL = makeThumbnails(defaultImg);
                            aggregator.getAlbum().updateImagePath(imgURL);
                            aggregator.getAlbum().updateThumbsPath(thumbsURL);
                        }
                        /*try {
                         release.newParseList();
                         System.out.println(release.getAlbumName());
                         if (folder != null) {
                         String coverURL = makeAlbumCover(folder);
                         String thumbsURL = makeThumbnails(folder);
                         release.getAlbum().updateCoverPath(coverURL);
                         release.getAlbum().updateThumbsPath(thumbsURL);
                         } else {
                         File defaultCover = new File("temp.png");
                         release.getAlbum().updateCoverPath(defaultCover.getAbsolutePath());
                         release.getAlbum().updateThumbsPath(defaultCover.getAbsolutePath());
                         }
                         } catch (CannotReadException ex) {
                         Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
                         } catch (TagException ex) {
                         Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
                         } catch (ReadOnlyFileException ex) {
                         Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
                         } catch (InvalidAudioFrameException ex) {
                         Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
                         }*/
                    } catch (CannotReadException ex) {
                        Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (TagException ex) {
                        Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (ReadOnlyFileException ex) {
                        Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
                    } catch (InvalidAudioFrameException ex) {
                        Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
                    }
                }
                System.out.println("");
            }
        }
        progress.setCount(list.size());
        progress.setStringArray(list);
        progress.run();

    }

    private int mp3DirAmount(File fileList[]) {
        int count = 0;
        for (int i = 0; i < fileList.length; i++) {
            if (fileList[i].getName().contains(".mp3")) {
                count++;
            }
        }
        return count;
    }

    private String makeAlbumImage(File imgFile) {
        File f = null;
        String dirName = imgFile.getAbsoluteFile().getParentFile().getName();
        try {
            File imgDir = new File(imageDir + "\\" + dirName.charAt(0));
            if (!imgDir.exists()) {
                imgDir.mkdir();
            }
            f = new File(imgDir, dirName + "_fld" + "." + getExtension(imgFile));
            FileUtils.copyFile(imgFile, f);
        } catch (IOException ex) {
            System.out.println("##### Ошибка копирования " + imgFile.getName());
        }
        return f.getAbsolutePath();
    }

    private String makeThumbnails(File imgFile) {
        File f = null;
        String dirName = imgFile.getAbsoluteFile().getParentFile().getName();
        try {
            File imgDir = new File(thumbsDir + "\\" + dirName.charAt(0));
            if (!imgDir.exists()) {
                imgDir.mkdir();
            }
            f = new File(imgDir, dirName + "_thumb" + "." + getExtension(imgFile));
            BufferedImage img = new BufferedImage(50, 50, BufferedImage.TYPE_INT_RGB);
            img.createGraphics().drawImage(ImageIO.read(imgFile).
                    getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
            ImageIO.write(img, getExtension(imgFile), f);
        } catch (IOException ex) {
            System.out.println("##### Ошибка копирования " + imgFile.getName());
        }
        return f.getAbsolutePath();
    }

    private String getExtension(File file) {
        String extension = "";
        int i = file.getName().lastIndexOf('.');
        if (i > 0) {
            extension = file.getName().substring(i + 1);
        }
        return extension;
    }
    /*
     private String artworkToFile(Release release, File file) {
     String dirName = file.getParentFile().getName();
     String path = imageDir + "\\" + dirName.charAt(7);
     File imgDir = new File(path);
     if (!imgDir.exists()) {
     imgDir.mkdir();
     }

     File f = new File(imgDir, dirName + "_fld" + ".jpg");

     try {
     BufferedImage img = release.getArtwork().getImage();
     img.createGraphics().drawImage(img, 0, 0, null);
     ImageIO.write(img, "jpg", f);
     } catch (FileNotFoundException ex) {
     System.out.println("##### Ошибка записи Artwork - FileNotFoundException");
     } catch (IOException ex) {
     System.out.println("##### Ошибка записи Artwork - IOException");
     }
     return f.getAbsolutePath();
     }

     private String artworkToThumb(Release release, File file) {
     String dirName = file.getParentFile().getName();
     String path = thumbsDir + "\\" + dirName.charAt(7);
     File imgDir = new File(path);
     if (!imgDir.exists()) {
     imgDir.mkdir();
     }

     File f = new File(imgDir, dirName + "_thumb" + ".jpg");

     try {
     BufferedImage img = release.getArtwork().getImage();
     img.createGraphics().drawImage(img.
     getScaledInstance(50, 50, java.awt.Image.SCALE_SMOOTH), 0, 0, null);
     ImageIO.write(img, "jpg", f);
     } catch (IOException ex) {
     Logger.getLogger(SampleController.class.getName()).log(Level.SEVERE, null, ex);
     }

     return f.getAbsolutePath();
     }
     */

    @Override
    public void initialize(URL url, ResourceBundle rb) {

        File dir = new File(initDir);
        File imgDir = new File(imageDir);
        File tmbDir = new File(thumbsDir);

        if (!dir.exists()) {
            dir.mkdir();
        }

        if (!imgDir.exists()) {
            imgDir.mkdir();
        }

        if (!tmbDir.exists()) {
            tmbDir.mkdir();
        }

        try {
            sqlWorker = new SqlWorker();
            mainTab = new MainTab(tabPane, sqlWorker);
            mainTab.init();
        } catch (SQLException ex) {
        }

        tabPane.setTabClosingPolicy(TabPane.TabClosingPolicy.ALL_TABS);

        listProducer = new ListProducer(tabPane, sqlWorker);
        listProducer.setAlbumsListView(albumsList);
        listProducer.setArtistListView(artistList);
        listProducer.init();
        
        /*collectionTreeView = new TreeViewWithItems();
        collectionAnchorPane.getChildren().add(collectionTreeView);
        collectionListProducer = new CollectionListProducer(tabPane, sqlWorker);
        collectionListProducer.setCollectionTreeView(collectionTreeView);
        collectionListProducer.init();*/
        
        /*leftPart = new LeftPart(tviewArtist, tviewAlbums, sqlWorker);
         try {
         leftPart.addRoot();
         } catch (Exception ex) {
         ex.printStackTrace();
         System.out.println("##### Ошибка загрузки дерева");
         }
         
         leftPart.settPane(tPane);
         */
    } 
}
