import com.google.gson.internal.LinkedHashTreeMap;

import javax.swing.*;
import java.awt.*;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseAdapter;
import java.awt.event.MouseEvent;
import java.io.IOException;
import java.util.Map;

public class ListMenuForRecording {
    final static Map<String, Tube> map = new LinkedHashTreeMap<String, Tube>();
    static long startPoint = 0;
    static boolean isASet = false;
    static int originSize = 0;
    static int indexAtSet = 0;
    static long endPoint = 0;
    public static String TEXT = "";
    private static String captionString;
    public static String fileName;

    public static void addMenuAndButton(GroupLayout.SequentialGroup horizontalGroup, GroupLayout.SequentialGroup verticalGroup, GroupLayout layout, JFrame jFrame, final String absolutePath, final String targetFileName) {

        setMap(absolutePath);


        final DefaultListModel model = new DefaultListModel();
        final JList list = new JList(model);
        JFrame f = new JFrame();
        f.setTitle("JList models");

        for (String key : map.keySet()) {
            model.addElement(key);
        }


        list.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);
        list.setBorder(BorderFactory.createEmptyBorder(2, 2, 2, 2));

        list.addMouseListener(new MouseAdapter() {
            public void mouseClicked(MouseEvent e) {
                if (e.getClickCount() == 2) {
                    int index = list.locationToIndex(e.getPoint());
                    Object item = model.getElementAt(index);
                    Tube args = map.get(item.toString());
                    System.out.println(args);
                    String[] split = args.time.split("-->");
                    String start = split[0];
                    String end = split[1];
                    PlayerMain.jumpToByMiniSec(UtilDate.formatTime(start.trim()), UtilDate.formatTime(end.trim()));
                    String newitem = "";

                    if (!newitem.isEmpty()) {
                        model.remove(index);
                        model.add(index, newitem);
                        ListSelectionModel selmodel = list.getSelectionModel();
                        selmodel.setLeadSelectionIndex(index);
                    }
                }
            }
        });


        JScrollPane scrollPanel = new JScrollPane(list);

        scrollPanel.setPreferredSize(new Dimension(450, 110));

        JButton btnRemoveall = new JButton("Remove All");
        JButton btnAddA = new JButton("Add-A");
        JButton btnAddB = new JButton("Add-B");
        JButton btnRepeat = new JButton("Repeat");
        JButton btnDelete = new JButton("Delete");
        JButton btnEdit = new JButton("Edit");


        btnAddA.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        try {
                            startPoint = SingleAudVid.stopWatch.getTime();
                            TEXT = JOptionPane.showInputDialog("Add a new item");
                            String item = null;

                            if (TEXT != null) {
                                item = (model.size() + 1) + "-" + TEXT.trim();
                            } else {
                                return;
                            }
                            isASet = true;
                            indexAtSet = model.size() + 1;

                            if (!item.isEmpty())
                                model.addElement(item);
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });

        btnAddB.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                Runnable runnable = new Runnable() {
                    public void run() {
                        try {
                            endPoint = SingleAudVid.stopWatch.getTime();
                            if (isASet) {
                                String saveToFile = null;
                                String timeString = null;
                                if (!captionString.isEmpty()) {
                                    saveToFile = "\n\n" + String.valueOf(indexAtSet);
                                    timeString = TimeUtil.getTimeString(startPoint, endPoint);
                                    saveToFile += "\n" + timeString;
                                    saveToFile += "\n" + TEXT + "\n";
                                } else {
                                    saveToFile = String.valueOf(indexAtSet);
                                    timeString = TimeUtil.getTimeString(startPoint, endPoint);
                                    saveToFile += "\n" + timeString;
                                    saveToFile += "\n" + TEXT + "\n";

                                }
                                Tube tube = new Tube();
                                tube.index = String.valueOf(indexAtSet);
                                tube.titleOnShow = indexAtSet + "-" + TEXT;
                                tube.title = TEXT;
                                tube.time = timeString;
                                tube.filePath = ConsLocal.RECORDING_FOLDER+ fileName + ".mkv";
// todo: integrate witht he solr searching
                                tube.toStringToSaveInSolr();
                                map.put(tube.titleOnShow, tube);
                                System.out.println(timeString);
                                isASet = false;
                                String toWrite = "";
                                for (Tube tube1 : map.values()) {
                                    toWrite += tube1.toString();
                                }
                                UtilIO.writeToTextFileByAbsolutePath(UtilString.removeRANDOM_CHARACTER(toWrite), ConsLocal.FOLDER + UtilFile.onlyFileName(targetFileName) + ".txt");
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                };
                Thread thread = new Thread(runnable);
                thread.start();
            }
        });

        btnEdit.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ListSelectionModel selmodel = list.getSelectionModel();
                int index = selmodel.getMinSelectionIndex();
                if (index >= 0) {
                    String text = JOptionPane.showInputDialog("Change the title");
                    String item = null;
                    if (text != null)
                        item = (index + 1) + "-" + text.trim();
                    else
                        return;

                    if (!item.isEmpty())
                        model.setElementAt(item, index);
                }
            }
        });

        btnDelete.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent event) {
                ListSelectionModel selmodel = list.getSelectionModel();
                int index = selmodel.getMinSelectionIndex();
                if (index >= 0)
                    model.remove(index);
            }

        });

        btnRepeat.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                ListSelectionModel selmodel = list.getSelectionModel();
                int index = selmodel.getMinSelectionIndex();
                if (index == -1)
                    return;
                Object item = model.getElementAt(index);
                PlayerMain.repeat = !PlayerMain.repeat;
            }
        });

        btnRemoveall.addActionListener(new ActionListener() {
            public void actionPerformed(ActionEvent e) {
                model.clear();
            }
        });


        if (jFrame instanceof Window) {

            Window window = (Window) jFrame;

            horizontalGroup
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(window.btnDubToggle)
                            .addComponent(window.btnPlay)
                            .addComponent(window.btnPause)
                            .addComponent(window.btnStop)
                            .addComponent(window.btnPingLu)
                            .addComponent(window.btnRecMySound)
                            .addComponent(window.btnYinPingLu)
                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(window.btnCopyToggle)
                            .addComponent(window.btnA)
                            .addComponent(window.btnB)
                            .addComponent(window.btnShot)
                            .addComponent(window.btnRecSysSound)
                            .addComponent(window.btnCutInPiece)
                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(scrollPanel)
                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                            .addComponent(btnAddA)
                            .addComponent(btnAddB)
                            .addComponent(btnDelete)
                            .addComponent(btnRepeat)
                            .addComponent(btnRemoveall)
                            .addComponent(btnEdit)
                    );
            ;

            verticalGroup
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(window.btnDubToggle)
                            .addComponent(window.btnCopyToggle)
                            .addComponent(scrollPanel)
                            .addComponent(btnAddA)
                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(window.btnPlay)
                            .addComponent(window.btnA)
                            .addComponent(btnAddB)

                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(window.btnPause)
                            .addComponent(window.btnB)
                            .addComponent(btnDelete)
                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(window.btnStop)
                            .addComponent(window.btnShot)
                            .addComponent(btnRepeat)
                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(window.btnPingLu)
                            .addComponent(window.btnRecSysSound)
                            .addComponent(btnRemoveall)

                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(window.btnRecMySound)
                            .addComponent(window.btnCutInPiece)
                            .addComponent(btnEdit)
                    )
                    .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                            .addComponent(window.btnYinPingLu)
                    );
        } else {
            if (jFrame instanceof SingleAudVid) {

                SingleAudVid single = (SingleAudVid) jFrame;


                horizontalGroup
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(scrollPanel)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.LEADING)
                                .addComponent(btnAddA)
                                .addComponent(btnAddB)
                                .addComponent(btnDelete)
                                .addComponent(btnRepeat)
                                .addComponent(btnRemoveall)
                                .addComponent(btnEdit)
                                .addComponent(single.button)
                        );


                verticalGroup
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(scrollPanel)
                                .addComponent(btnAddA)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnAddB)

                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnDelete)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnRepeat)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnRemoveall)

                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                                .addComponent(btnEdit)
                        )
                        .addGroup(layout.createParallelGroup(GroupLayout.Alignment.BASELINE)
                        );
            }
        }

    }

    private static void setMap(String absolutePath) {
        try {
            captionString = UtilString.removeRANDOM_CHARACTER(UtilIO.readFileAsString(absolutePath));

            if (!captionString.isEmpty()) {
                String[] strings = UtilString.splitStringAsArrayByLineBreak(captionString);
                for (int i = 0; i < strings.length; i = i + 5) {
                    Tube tube = new Tube();
                    tube.titleOnShow = strings[i];
                    tube.index = strings[i];
                    tube.time = strings[i + 1];
                    tube.title = "";
                    if (i + 2 < strings.length) {
                        tube.title = strings[i + 2];
                        tube.secondTitle = strings[i + 3];
                        tube.titleOnShow = strings[i] + "-" + tube.title;

                        tube.filePath = ConsLocal.RECORDING_FOLDER+ fileName + ".mkv";
                    }
                    map.put(tube.titleOnShow, tube);
                }
            }
            originSize = map.size();
        } catch (IOException e) {
            e.printStackTrace();
        }

    }


}


