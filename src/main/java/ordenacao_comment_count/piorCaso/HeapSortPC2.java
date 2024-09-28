package main.java.ordenacao_comment_count.piorCaso;

import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class HeapSortPC2 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Path inputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_heapSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_heapSort_piorCaso.csv");
        int columnIndex = 11;

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex);

            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data);

            if (isAlreadySortedDescending(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados em ordem decrescente. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação...");
                try {
                    sortByCommentCountHeapSortDesc(data, columnIndex);
                    System.out.println("Ordenação finalizada. Salvando arquivo...");
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            writeCsv(data, outputPath);
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    public static void sortByCommentCountHeapSortDesc(String[][] data, int columnIndex) {
        if (data.length <= 1) {
            System.err.println("Lista de dados está vazia ou contém apenas o cabeçalho.");
            return;
        }

        if (columnIndex < 0 || data[0].length <= columnIndex) {
            System.err.println("Índice de coluna inválido: " + columnIndex);
            return;
        }

        String[] header = data[0];
        String[][] dataToSort = Arrays.copyOfRange(data, 1, data.length);

        System.out.println("Iniciando HeapSort para ordenação decrescente na coluna: " + columnIndex);

        int n = dataToSort.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapifyDesc(dataToSort, n, i, columnIndex);
        }

        for (int i = n - 1; i >= 0; i--) {
            String[] temp = dataToSort[0];
            dataToSort[0] = dataToSort[i];
            dataToSort[i] = temp;
            System.out.println("Elemento na posição " + i + " trocado com a raiz.");

            heapifyDesc(dataToSort, i, 0, columnIndex);
        }

        String[][] sortedData = new String[data.length][];
        sortedData[0] = header;
        System.arraycopy(dataToSort, 0, sortedData, 1, dataToSort.length);
        System.arraycopy(sortedData, 0, data, 0, sortedData.length);

        System.out.println("Ordenação HeapSort decrescente concluída.");
    }

    private static void heapifyDesc(String[][] data, int n, int i, int columnIndex) {
        int largest = i;
        int left = 2 * i + 1;
        int right = 2 * i + 2;

        try {
            if (left < n && Integer.parseInt(data[left][columnIndex].trim()) < Integer.parseInt(data[largest][columnIndex].trim())) {
                largest = left;
                System.out.println("Filho à esquerda " + Arrays.toString(data[left]) + " é menor que " + Arrays.toString(data[i]));
            }

            if (right < n && Integer.parseInt(data[right][columnIndex].trim()) < Integer.parseInt(data[largest][columnIndex].trim())) {
                largest = right;
                System.out.println("Filho à direita " + Arrays.toString(data[right]) + " é menor que " + Arrays.toString(data[largest]));
            }

            if (largest != i) {
                String[] swap = data[i];
                data[i] = data[largest];
                data[largest] = swap;

                System.out.println("Trocando " + Arrays.toString(data[i]) + " com " + Arrays.toString(data[largest]));

                heapifyDesc(data, n, largest, columnIndex);
            }
        } catch (NumberFormatException e) {
            System.err.println("Erro ao converter para número: " + e.getMessage());
        }
    }

    public static boolean isAlreadySortedDescending(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length - 1; i++) {
            try {
                String currentValueStr = data[i][columnIndex] != null ? data[i][columnIndex].trim() : "";
                String nextValueStr = data[i + 1][columnIndex] != null ? data[i + 1][columnIndex].trim() : "";

                if (currentValueStr.isEmpty() || nextValueStr.isEmpty()) {
                    System.out.println("Ignorando linha " + i + " ou " + (i + 1) + " por ter valor vazio.");
                    continue;
                }

                int currentValue = Integer.parseInt(currentValueStr);
                int nextValue = Integer.parseInt(nextValueStr);

                if (currentValue < nextValue) {
                    System.out.println("Dados não estão em ordem decrescente na linha " + i + ": " + currentValue + " < " + nextValue);
                    return false;
                }
            } catch (NumberFormatException e) {
                System.out.println("Ignorando linha " + i + " devido a um erro de formato: " + data[i][columnIndex]);
            }
        }
        return true;
    }

    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine();
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(",");
            data = new String[1][headers.length];
            data[0] = headers;

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim()
                    .withQuoteMode(QuoteMode.ALL);

            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1;
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1);
                data[rowCount] = record.stream().toArray(String[]::new);
                System.out.println("Linha " + rowCount + " lida: " + Arrays.toString(data[rowCount]));
                rowCount++;
            }
            System.out.println("Total de linhas lidas: " + rowCount);
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return data;
    }

    private static String[][] expandArray(String[][] original, int newSize) {
        return Arrays.copyOf(original, newSize);
    }

    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length; i++) {
            if (data[i][columnIndex] != null) {
                data[i][columnIndex] = data[i][columnIndex].trim();
            }
        }
    }

    public static void fillMissingColumns(String[][] data) {
        for (int i = 1; i < data.length; i++) {
            if (data[i].length < data[0].length) {
                String[] filledRow = Arrays.copyOf(data[i], data[0].length);
                Arrays.fill(filledRow, data[i].length, filledRow.length, "");
                data[i] = filledRow;
                System.out.println("Colunas preenchidas na linha " + i + ": " + Arrays.toString(data[i]));
            }
        }
    }

    public static void writeCsv(String[][] data, Path outputPath) {
        try (CSVPrinter csvPrinter = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT.withHeader(data[0]))) {
            for (int i = 1; i < data.length; i++) {
                csvPrinter.printRecord((Object[]) data[i]);
            }
            System.out.println("Arquivo CSV salvo em: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
        }
    }
}