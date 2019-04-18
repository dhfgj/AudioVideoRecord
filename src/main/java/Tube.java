/**
 * Created by Owner on 2/15/2018.
 */
public class Tube {
    @Override
    public String toString() {
        return index + '\n' +
                time + "\n" +
                title + "\n" +
                secondTitle + "\n" +
                filePath + "\n";
    }

    public void toStringToSaveInSolr() {
        try {
            if (UtilString.isNotEmpty(title)) {
                String firstTitle = title + "=" +
                        time + "," +
                        filePath;
                Come2Me.process(firstTitle);
            }
            if (UtilString.isNotEmpty(title)) {
                String secondTitleToSave = secondTitle + "=" +
                        time + "," +
                        filePath;
                Come2Me.process(secondTitleToSave);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    public String titleOnShow = "";
    public String index = "";
    public String time = "";
    public String title = "";
    public String secondTitle = "";
    public String filePath;
}
