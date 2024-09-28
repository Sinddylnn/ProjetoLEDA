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

public class SelectionSort {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis(); // Inicia contagem do tempo

        // Caminho do arquivo de entrada e saída
        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_selectionSort_medioCaso.csv");
        int columnIndex = 4; // Índice da coluna "channel_title"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Lê o arquivo CSV
        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex); // Limpa espaços na coluna especificada

            System.out.println("Valores da coluna channel_title antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][columnIndex]); // Mostra valores antes da ordenação
            }

            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data); // Preenche linhas com colunas ausentes

            // Verifica se já está ordenado em ordem crescente
            if (isAlreadySorted(data, columnIndex, true)) {
                System.out.println("Os dados já estão ordenados em ordem crescente. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação por Selection Sort...");
                try {
                    sortByChannelTitle(data, columnIndex); // Ordena os dados
                    System.out.println("Ordenação por Selection Sort finalizada. Salvando arquivo...");
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                    e.printStackTrace();
                }
            }

            writeCsv(data, outputPath); // Grava os dados ordenados no arquivo
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        // Exibe o tempo de execução e a memória utilizada
        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Ordena os dados pela coluna especificada usando o algoritmo Selection Sort
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
        String[][] dataToSort = Arrays.copyOfRange(data, 1, data.length); // Cópia dos dados sem cabeçalho

        // Ordena pelo título do canal
        System.out.println("Iniciando ordenação pelo título do canal...");
        for (int i = 0; i < dataToSort.length; i++) { // Ajuste para começar de 0
            String[] keyRow = dataToSort[i];
            String keyValue = keyRow[columnIndex].trim();
            int j = i - 1;

            // Move os elementos que são maiores que keyValue para uma posição à frente
            while (j >= 0 && dataToSort[j][columnIndex].trim().compareToIgnoreCase(keyValue) > 0) {
                dataToSort[j + 1] = dataToSort[j];
                j = j - 1;
            }
            dataToSort[j + 1] = keyRow; // Coloca keyRow na posição correta
            System.out.println("Colocando a linha " + (i + 1) + " na posição " + (j + 2));
        }

        // Concatenar o cabeçalho de volta aos dados ordenados
        String[][] sortedData = new String[data.length][];
        sortedData[0] = header;
        System.arraycopy(dataToSort, 0, sortedData, 1, dataToSort.length);
        System.arraycopy(sortedData, 0, data, 0, sortedData.length);
        System.out.println("Ordenação concluída.");
    }

    // Lê os dados de um arquivo CSV e retorna um array bidimensional
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][];
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê a primeira linha como cabeçalho
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(","); // Divide o cabeçalho por vírgulas
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
                data = expandArray(data, rowCount + 1); // Expande o array para incluir mais linhas
                data[rowCount] = record.stream().toArray(String[]::new); // Adiciona a linha lida
                rowCount++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
        }
        return data;
    }

    // Remove espaços em branco das colunas especificadas
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) { // Começa a partir da linha 1 para evitar o cabeçalho
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove espaços em branco
            }
        }
    }

    // Preenche linhas que têm menos colunas do que o cabeçalho
    public static void fillMissingColumns(String[][] data) {
        int maxColumns = Arrays.stream(data).mapToInt(row -> row.length).max().orElse(0);
        for (int i = 0; i < data.length; i++) {
            if (data[i].length < maxColumns) {
                data[i] = Arrays.copyOf(data[i], maxColumns); // Preenche colunas ausentes
            }
        }
    }

    // Verifica se os dados estão ordenados na coluna especificada
    public static boolean isAlreadySorted(String[][] data, int columnIndex, boolean ascending) {
        for (int i = 1; i < data.length - 1; i++) {
            if (ascending) {
                // Verifica se está ordenado em ordem crescente
                if (data[i][columnIndex].compareToIgnoreCase(data[i + 1][columnIndex]) > 0) {
                    return false; // Não está ordenado em ordem crescente
                }
            } else {
                // Verifica se está ordenado em ordem decrescente
                if (data[i][columnIndex].compareToIgnoreCase(data[i + 1][columnIndex]) < 0) {
                    return false; // Não está ordenado em ordem decrescente
                }
            }
        }
        return true; // Dados estão ordenados
    }

    // Grava os dados em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL))) {
            for (String[] row : data) {
                printer.printRecord(row); // Grava cada linha no arquivo
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Expande um array bidimensional para incluir mais linhas
    public static String[][] expandArray(String[][] original, int newSize) {
        String[][] expanded = new String[newSize][original[0].length];
        for (int i = 0; i < original.length; i++) {
            expanded[i] = original[i];
        }
        return expanded;
    }
}
