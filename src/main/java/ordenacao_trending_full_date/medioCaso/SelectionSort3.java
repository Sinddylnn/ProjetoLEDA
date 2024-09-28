package main.java.ordenacao_trending_full_date.medioCaso;

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

public class SelectionSort3 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_selectionSort_medioCaso.csv");
        int dateIndex = 2; // Índice da coluna "trending_full_date"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        String[][] data = readCsv(inputPath); // Lê o arquivo CSV

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, dateIndex); // Limpa espaços na coluna de data

            System.out.println("Valores da coluna trending_full_date antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            System.out.println("Iniciando ordenação por trending_full_date em ordem decrescente...");
            selectionSort(data, dateIndex); // Ordena a partir do índice da coluna de data

            // Verifica se o arquivo está ordenado em ordem decrescente
            if (isSortedDescending(data, dateIndex)) {
                System.out.println("O arquivo já está ordenado em ordem decrescente.");
            } else {
                System.out.println("O arquivo não está ordenado corretamente.");
            }

            writeCsv(data, outputPath); // Grava o arquivo ordenado
            System.out.println("Arquivo ordenado salvo em: " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Método de Selection Sort para ordenar a matriz com base na coluna de data em ordem decrescente
    public static void selectionSort(String[][] data, int columnIndex) {
        int n = data.length;

        for (int i = 1; i < n - 1; i++) {
            // Encontra o índice do maior elemento
            int maxIndex = i;
            for (int j = i + 1; j < n; j++) {
                if (compareDates(data[j][columnIndex], data[maxIndex][columnIndex]) > 0) {
                    maxIndex = j;
                }
            }

            // Troca o maior elemento encontrado com o primeiro elemento
            if (maxIndex != i) {
                swap(data, i, maxIndex);
                System.out.println("Trocando " + data[i][columnIndex] + " com " + data[maxIndex][columnIndex]);
            }
        }
    }

    // Método para trocar elementos no array
    private static void swap(String[][] data, int i, int j) {
        String[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // Método para comparar as datas
    public static int compareDates(String date1, String date2) {
        // Formato da data: dd/MM/yyyy
        String[] parts1 = date1.split("/");
        String[] parts2 = date2.split("/");

        // Compara os anos
        int yearComparison = Integer.compare(Integer.parseInt(parts1[2]), Integer.parseInt(parts2[2]));
        if (yearComparison != 0) return yearComparison;

        // Compara os meses
        int monthComparison = Integer.compare(Integer.parseInt(parts1[1]), Integer.parseInt(parts2[1]));
        if (monthComparison != 0) return monthComparison;

        // Compara os dias
        return Integer.compare(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]));
    }

    // Método para ler um arquivo CSV e retornar os dados em uma matriz
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

    // Método para limpar espaços em branco nas colunas de uma matriz
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove espaços em branco
            }
        }
    }

    // Método para expandir a matriz original para um novo comprimento
    private static String[][] expandArray(String[][] original, int newLength) {
        String[][] newArray = new String[newLength][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }

    // Método para gravar dados em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            for (String[] row : data) {
                printer.printRecord(row);
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para verificar se o arquivo está ordenado em ordem decrescente
    public static boolean isSortedDescending(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length - 1; i++) {
            if (compareDates(data[i][columnIndex], data[i + 1][columnIndex]) < 0) {
                return false; // Se encontrar um par fora de ordem, retorna false
            }
        }
        return true; // Se todos os pares estão na ordem correta, retorna true
    }
}
