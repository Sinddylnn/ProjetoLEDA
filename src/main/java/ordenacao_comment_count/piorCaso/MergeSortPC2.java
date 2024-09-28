package main.java.ordenacao_comment_count.piorCaso;

import org.apache.commons.csv.*;

import java.io.BufferedReader;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Arrays;

public class MergeSortPC2 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Path inputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_mergeSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_comment_count_mergeSort_piorCaso.csv");
        int columnIndex = 11; // Índice da coluna "comment_count"

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

            if (isAlreadySorted(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação decrescente...");
                try {
                    sortByCommentCount(data, columnIndex);
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

    public static boolean isAlreadySorted(String[][] data, int columnIndex) {
        for (int i = 2; i < data.length; i++) {
            String[] previousRow = data[i - 1];
            String[] currentRow = data[i];

            if (previousRow.length <= columnIndex || currentRow.length <= columnIndex) {
                continue;
            }

            String previousValue = previousRow[columnIndex].trim();
            String currentValue = currentRow[columnIndex].trim();

            if (previousValue.isEmpty() || currentValue.isEmpty()) {
                continue;
            }

            try {
                int previousCount = Integer.parseInt(previousValue);
                int currentCount = Integer.parseInt(currentValue);
                if (previousCount < currentCount) { // Verificação para decrescente
                    return false;
                }
            } catch (NumberFormatException e) {
                System.err.println("Erro ao converter valores para inteiros: " + previousValue + " ou " + currentValue);
                return false;
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

            CSVFormat csvFormat = CSVFormat.DEFAULT.withTrim().withQuoteMode(QuoteMode.ALL);

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
            e.printStackTrace();
        }
        return data;
    }

    public static String[][] expandArray(String[][] original, int newSize) {
        String[][] newArray = new String[newSize][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }

    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Removendo espaços iniciais na coluna " + columnIndex + "...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = removeLeadingAndTrailingSpaces(row[columnIndex]);
                System.out.println("Linha " + i + " após limpeza: " + Arrays.toString(row));
            }
        }
    }

    private static String removeLeadingAndTrailingSpaces(String input) {
        return input.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
    }

    public static void fillMissingColumns(String[][] data) {
        String[] headers = data[0];
        int numColumns = headers.length;

        System.out.println("Preenchendo colunas ausentes...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length < numColumns) {
                String[] newRow = new String[numColumns];
                System.arraycopy(row, 0, newRow, 0, row.length);
                for (int j = row.length; j < numColumns; j++) {
                    newRow[j] = "";
                }
                data[i] = newRow;
                System.out.println("Linha " + i + " após preenchimento: " + Arrays.toString(newRow));
            }
        }
    }

    public static void sortByCommentCount(String[][] data, int columnIndex) {
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

        System.out.println("Iniciando ordenação pela contagem de comentários...");
        dataToSort = mergeSort(dataToSort, columnIndex);
        System.out.println("Ordenação concluída.");

        String[][] sortedData = new String[data.length][];
        sortedData[0] = header;
        System.arraycopy(dataToSort, 0, sortedData, 1, dataToSort.length);
        System.arraycopy(sortedData, 0, data, 0, sortedData.length);
    }

    private static String[][] mergeSort(String[][] dataToSort, int columnIndex) {
        if (dataToSort.length <= 1) {
            return dataToSort;
        }

        int mid = dataToSort.length / 2;
        String[][] left = Arrays.copyOfRange(dataToSort, 0, mid);
        String[][] right = Arrays.copyOfRange(dataToSort, mid, dataToSort.length);

        left = mergeSort(left, columnIndex);
        right = mergeSort(right, columnIndex);

        return merge(left, right, columnIndex);
    }

    private static String[][] merge(String[][] left, String[][] right, int columnIndex) {
        String[][] merged = new String[left.length + right.length][];
        int i = 0, j = 0, k = 0;

        while (i < left.length && j < right.length) {
            if (!isLessThan(left[i][columnIndex], right[j][columnIndex])) { // Mudança para decrescente
                merged[k++] = left[i++];
            } else {
                merged[k++] = right[j++];
            }
        }

        while (i < left.length) {
            merged[k++] = left[i++];
        }

        while (j < right.length) {
            merged[k++] = right[j++];
        }

        return merged;
    }

    private static boolean isLessThan(String a, String b) {
        try {
            int valueA = Integer.parseInt(a);
            int valueB = Integer.parseInt(b);
            return valueA < valueB; // Ordem decrescente
        } catch (NumberFormatException e) {
            System.err.println("Erro ao comparar valores: " + a + " e " + b);
            return false;
        }
    }

    public static void writeCsv(String[][] data, Path outputPath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            for (String[] row : data) {
                printer.printRecord((Object[]) row);
            }
            System.out.println("Arquivo CSV gravado com sucesso em: " + outputPath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao gravar o arquivo CSV: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

