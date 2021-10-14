package ru.geekbrains.july_ch;


import java.io.*;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

public class History {
    private String login;
    private static final int RECOVERABLE_MESSAGE_QUANTITY = 100;
    private static final String HISTORY_PATH = "history/";
    private File messageHistoryFile;


    public History(String login) {
        this.login = login;
        this.messageHistoryFile = new File(HISTORY_PATH + "history" + login + ".txt");
        if(!messageHistoryFile.exists()){
            File file = new File(HISTORY_PATH);
            file.mkdirs();
        }
    }
    public List<String> readHistory(){
        if (!messageHistoryFile.exists()) return Collections.singletonList("NO previous history");
        List<String> res = null;
        if(messageHistoryFile.exists()){
            try (BufferedReader reader = new BufferedReader(new FileReader(messageHistoryFile))){
                String historyString;
                List<String> historyStrings = new ArrayList<>();
                while ((historyString = reader.readLine()) != null){
                    historyStrings.add(historyString);
                }
                if (historyStrings.size() <= RECOVERABLE_MESSAGE_QUANTITY){
                    res = historyStrings;
                //
                }if (historyStrings.size() > RECOVERABLE_MESSAGE_QUANTITY){
                    int firstIndex = historyStrings.size() - RECOVERABLE_MESSAGE_QUANTITY;
                    res = new ArrayList<>(RECOVERABLE_MESSAGE_QUANTITY);

                    for (int counter = firstIndex - 1; counter < historyStrings.size() ; counter++) {
                        res.add(historyStrings.get(counter));

                    }
                }

            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        System.out.println("History for" + res.size());
        return res;
    }
    public void writeHistory( String message){
        try (BufferedWriter writer = new BufferedWriter(new FileWriter(messageHistoryFile, true))){
            writer.write(message);
        } catch (IOException e) {
            e.printStackTrace();
        }


    }
}