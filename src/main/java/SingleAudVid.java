import com.sun.jna.NativeLibrary;
import org.apache.commons.lang.time.StopWatch;
import uk.co.caprica.vlcj.runtime.RuntimeUtil;

import javax.swing.*;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;

//import statements
//Check if window closes automatically. Otherwise add suitable code
public class SingleAudVid extends JFrame {
    public static JToggleButton button;
    public CustomThread thread = null;
    public JPanel controlPanel;  //控制按钮容器
    public static  StopWatch stopWatch;

    public static void main(String args[]) {
        new SingleAudVid();
    }

    SingleAudVid() {
        System.out.println(System.getProperty("user.dir"));
        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:\\Program Files (x86)\\Java\\jdk1.8.0_151\\lib\\win32-x86");  //导入的路径是vlc的安装路径

//        NativeLibrary.addSearchPath(RuntimeUtil.getLibVlcLibraryName(), "C:/o/lib");
        System.setProperty("VLC_PLUGIN_PATH", "C:/o/plugins");


        button = new JToggleButton(this.getClass().getName());
        button.addMouseListener(new MouseAdapter() {
            @Override
            public void mouseClicked(MouseEvent e) {
                if (null == thread) {
                    thread = new YinPingLu();
                    stopWatch = new StopWatch();
                    stopWatch.start();
                    thread.start();
                } else {
                    stopWatch.stop();

                    thread.stopRunning();
                    thread = null;
                }
            }
        });

        add(button);
        controlPanel = new JPanel();  //实例化控制按钮容器

        GroupLayout layout = new GroupLayout(controlPanel);
        layout.setAutoCreateGaps(true);
        layout.setAutoCreateContainerGaps(true);

        GroupLayout.SequentialGroup horizontalGroup = layout.createSequentialGroup();
        GroupLayout.SequentialGroup verticalGroup = layout.createSequentialGroup();


//        String fileName = PlayerMain.FILE_NAME;
        YinPingLu.fileName  = UtilPopUp.setFileName("Name it.");

        while (UtilFile.isFileExist(ConsLocal.FOLDER + YinPingLu.fileName  + ".txt")) {
            YinPingLu.fileName  = UtilPopUp.setFileName("File already exist.");
        }

        ListMenuForRecording.fileName = YinPingLu.fileName;


        ListMenuForRecording.addMenuAndButton(horizontalGroup, verticalGroup, layout, this, ConsLocal.FOLDER + YinPingLu.fileName + ".txt", YinPingLu.fileName );

        this.setSize(1600, 1000);

        add(controlPanel);
        setVisible(true);
    }


}