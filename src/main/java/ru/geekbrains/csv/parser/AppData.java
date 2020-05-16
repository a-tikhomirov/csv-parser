package ru.geekbrains.csv.parser;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.nio.charset.Charset;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

public class AppData {
    private String[] header;
    private int[][] data;

    private void getDataFromList(List<String> data) throws Exception {
        if (data.isEmpty()) {
            throw new Exception("No input data provided");
        }

        header = data.get(0).split(";");
        this.data = new int[data.size() - 1][header.length];

        for (int i = 1; i < data.size(); ++i) {
            String[] dataLine = data.get(i).split(";");
            if (dataLine.length != header.length) {
                throw new Exception("Line " + (i + 1) + ": number of values not equal to headers number");
            }
            for (int j = 0; j < dataLine.length; ++j) {
                this.data[i - 1][j] = Integer.parseInt(dataLine[j]);
            }
        }
    }

    public AppData(List<String> data) {
        try {
            getDataFromList(data);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public AppData(Path path) {
       read(path);
    }

    public AppData(Path path, Charset charset) {
        read(path, charset);
    }

    public void read(Path path) {
        read(path, Charset.defaultCharset());
    }

    public void read(Path path, Charset charset) {
        try {
            getDataFromList(Files.readAllLines(path, charset));
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void checkData() {
        if (header == null || data == null) {
            throw new NullPointerException("No data in AppData object");
        }
    }

    private String getHeaderString() {
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < header.length - 1; ++i) {
            sb.append(header[i]).append(";");
        }
        sb.append(header[header.length - 1]).append("\n");
        return sb.toString();
    }

    public void save(Path path) {
        try (OutputStream out = new BufferedOutputStream(new FileOutputStream(path.toFile()))) {
            /*
             * Запись через this.toCsvString().getBytes()
             * отличается по времени для значительного объема данных:
             * например, для заголовка из 9 элементов по 900000 значений для каждого заголовка
             * время записи в файл будет составлять порядка 2,4 сек,
             * в отличие от используемого кода - 0,3~0,4 сек
             */
            //out.write(this.toCsvString().getBytes());

            checkData();
            out.write(getHeaderString().getBytes());
            StringBuilder sb = new StringBuilder();
            for (int[] dataArray:data) {
                // создание нового экземпляра незначительно, но увеличивает время для больших объемов данных
                //sb = new StringBuilder();
                sb.setLength(0);
                for (int i = 0; i < dataArray.length - 1; ++i) {
                    sb.append(dataArray[i]).append(";");
                }
                sb.append(dataArray[dataArray.length - 1]).append("\n");
                out.write(sb.toString().getBytes());
            }
        } catch (IOException|NullPointerException e) {
            e.printStackTrace();
        }
    }

    public String toCsvString() {
        checkData();
        StringBuilder sb = new StringBuilder();
        sb.append(getHeaderString());
        for (int[] dataArray:data) {
            for (int i = 0; i < dataArray.length - 1; ++i) {
                sb.append(dataArray[i]).append(";");
            }
            sb.append(dataArray[dataArray.length - 1]).append("\n");
        }
        return sb.toString();
    }

    @Override
    public String toString() {
        checkData();

        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < header.length; ++i) {
            sb.append(header[i]).append(":\t");
            for (int[] values : data) {
                sb.append(values[i]).append("; ");
            }
            sb.append("\n");
        }
        return sb.toString();
    }
}
