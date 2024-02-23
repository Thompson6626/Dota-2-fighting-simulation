package org.example;

import org.example.WebScrape.DataFetcher;
import java.util.List;

public class Main  {

    public static void main(String[] args) {

        //SwingUtilities.invokeLater(MenuFrame::new);

        List<String> items = DataFetcher.getAllItems();

        for(String str:items){
            DataFetcher.getItem(str);
        }
    }
}