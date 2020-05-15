package ru.geekbrains.csv.parser;

import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

public class MainApp {
    public static void main(String[] args) {
        AppData appData = new AppData(Paths.get("data.csv"));
        System.out.printf("appData: created AppData from data.csv:\n%s\n", appData.toString());

        int headers = 5;
        int values = 9;
        AppData appData2 = new AppData(genData(headers,values));
        appData2.save(Paths.get("data1.csv"));
        System.out.printf("appData2: created AppData from List(%d headers, %d values for each header) and saved it to data1.csv:\n%s\n", headers, values, appData2.toString());

        appData.read(Paths.get("data1.csv"));
        System.out.printf("appData: read from data1.csv:\n%s\n", appData.toCsvString());
    }

    private static List<String> genData(int headersCount, int valuesCount) {
        List<String> data = new ArrayList<>();
        if (headersCount == 0 || valuesCount == 0) {
            return data;
        }

        StringBuilder sb = new StringBuilder();
        for (int i = 1; i < headersCount; ++i) {
            sb.append("Val").append(i).append(";");
        }
        sb.append("Val").append(headersCount);
        data.add(sb.toString());

        for (int i = 1; i < valuesCount + 1; ++i) {
            sb = new StringBuilder();
            for (int j = 1; j < headersCount; ++j) {
                sb.append(100 * i).append(j).append(";");
            }
            sb.append(100*i + headersCount);
            data.add(sb.toString());
        }
        return data;
    }
}
