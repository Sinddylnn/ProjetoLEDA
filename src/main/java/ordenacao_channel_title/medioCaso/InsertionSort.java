package main.java.ordenacao_channel_title.medioCaso;

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

public class InsertionSort {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Caminhos de entrada e saída ajustados
        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_insertionSort_medioCaso.csv");
        int columnIndex = 4; // Índice da coluna "channel_title"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe antes de tentar ler
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Carregar dados do CSV
        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex);

            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data);

            // Verifica se já está ordenado
            if (isAlreadySorted(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados em ordem crescente. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação crescente...");
                try {
                    sortByChannelTitle(data, columnIndex);
                    System.out.println("Ordenação finalizada. Salvando arquivo...");
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            // Escreve os dados no arquivo CSV de saída
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

    // Verifica se os dados já estão em ordem crescente
    public static boolean isAlreadySorted(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length; i++) {
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

            if (previousValue.compareToIgnoreCase(currentValue) > 0) {
                return false; // Se a linha anterior for maior, não está ordenado
            }
        }
        return true; // Se não encontrar problemas, retorna true (ordenado em ordem crescente)
    }

    // Lê o arquivo CSV e retorna os dados como um array bidimensional
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
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
        }
        return data;
    }

    // Expande um array bidimensional
    public static String[][] expandArray(String[][] original, int newSize) {
        String[][] newArray = new String[newSize][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }

    // Remove espaços iniciais e finais da coluna especificada
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

    // Remove espaços iniciais e finais de uma string
    private static String removeLeadingAndTrailingSpaces(String input) {
        return input.replaceAll("^\\s+", "").replaceAll("\\s+$", "");
    }

    // Preenche colunas ausentes com strings vazias
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

    // Ordenação crescente pelo título do canal
    public static void sortByChannelTitle(String[][] data, int columnIndex) {
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

        System.out.println("Iniciando ordenação crescente pelo título do canal...");
        for (int i = 1; i < dataToSort.length; i++) {
            String[] currentRow = dataToSort[i];
            String[] keyRow = currentRow;
            String keyValue = keyRow[columnIndex].trim();
            int j = i - 1;

            // Realiza a inserção ordenada
            while (j >= 0 && dataToSort[j][columnIndex].trim().compareToIgnoreCase(keyValue) > 0) {
                dataToSort[j + 1] = dataToSort[j];
                j = j - 1;
            }
            dataToSort[j + 1] = keyRow;
            System.out.println("Colocando a linha " + (i + 1) + " na posição " + (j + 2));
        }

        // Concatenar o cabeçalho de volta aos dados ordenados
        String[][] sortedData = new String[data.length][];
        sortedData[0] = header;
        System.arraycopy(dataToSort, 0, sortedData, 1, dataToSort.length);

        System.out.println("Ordenação finalizada.");
        // Substitui os dados não ordenados pelos dados ordenados
        System.arraycopy(sortedData, 0, data, 0, sortedData.length);
    }

    // Grava os dados em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            for (String[] row : data) {
                printer.printRecord(Arrays.asList(row));
            }
            System.out.println("Arquivo CSV salvo em: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
