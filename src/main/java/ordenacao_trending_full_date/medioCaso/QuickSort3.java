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

public class QuickSort3 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_quickSort_medioCaso.csv");
        int dateIndex = 2; // Índice da coluna "trending_full_date"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        String[][] data = readCsv(inputPath); // Lê o arquivo CSV

        // Verifica se os dados foram lidos corretamente
        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, dateIndex); // Remove espaços iniciais

            System.out.println("Valores da coluna trending_full_date antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Verifica se o CSV já está ordenado em ordem decrescente
            if (isSortedDescending(data, dateIndex)) {
                System.out.println("O arquivo já está ordenado em ordem decrescente. Saindo sem ordenar.");
            } else {
                System.out.println("Iniciando ordenação por trending_full_date em ordem decrescente...");
                quickSort(data, 1, data.length - 1, dateIndex); // Ordena os dados

                writeCsv(data, outputPath); // Grava os dados ordenados no novo arquivo
                System.out.println("Arquivo ordenado salvo em: " + outputPath.toAbsolutePath());
            }
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Método para verificar se a coluna está ordenada em ordem decrescente
    private static boolean isSortedDescending(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length - 1; i++) {
            if (compareDates(data[i][columnIndex], data[i + 1][columnIndex]) < 0) {
                return false; // Retorna false se encontrar uma ordem incorreta
            }
        }
        return true; // Retorna true se estiver tudo ordenado
    }

    // Método para executar Quick Sort
    public static void quickSort(String[][] data, int low, int high, int columnIndex) {
        if (low < high) {
            int partitionIndex = partition(data, low, high, columnIndex); // Particiona o array

            // Mensagens de depuração
            System.out.println("Particionando: baixo = " + low + ", alto = " + high + ", índice da partição = " + partitionIndex);
            System.out.println("Estado atual do array:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(Arrays.toString(data[i]));
            }

            quickSort(data, low, partitionIndex - 1, columnIndex); // Recursão à esquerda
            quickSort(data, partitionIndex + 1, high, columnIndex); // Recursão à direita
        }
    }

    // Método para particionar o array
    private static int partition(String[][] data, int low, int high, int columnIndex) {
        String pivot = data[high][columnIndex]; // Pivô
        int i = (low - 1); // Índice do menor elemento

        for (int j = low; j < high; j++) {
            if (compareDates(data[j][columnIndex], pivot) > 0) { // Para ordem decrescente
                i++;

                // Trocar data[i] e data[j]
                String[] temp = data[i];
                data[i] = data[j];
                data[j] = temp;

                // Mensagem de depuração
                System.out.println("Trocando " + Arrays.toString(data[i]) + " com " + Arrays.toString(data[j]));
            }
        }

        // Trocar data[i + 1] e data[high] (ou pivô)
        String[] temp = data[i + 1];
        data[i + 1] = data[high];
        data[high] = temp;

        // Mensagem de depuração
        System.out.println("Colocando pivô " + Arrays.toString(data[i + 1]) + " na posição correta.");

        return i + 1;
    }

    // Método para comparar datas
    private static int compareDates(String date1, String date2) {
        String[] date1Parts = date1.split("/");
        String[] date2Parts = date2.split("/");

        int year1 = Integer.parseInt(date1Parts[2]);
        int month1 = Integer.parseInt(date1Parts[1]);
        int day1 = Integer.parseInt(date1Parts[0]);

        int year2 = Integer.parseInt(date2Parts[2]);
        int month2 = Integer.parseInt(date2Parts[1]);
        int day2 = Integer.parseInt(date2Parts[0]);

        if (year1 != year2) {
            return Integer.compare(year2, year1); // Decrescente
        } else if (month1 != month2) {
            return Integer.compare(month2, month1); // Decrescente
        } else {
            return Integer.compare(day2, day1); // Decrescente
        }
    }

    // Método para ler o arquivo CSV
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

    // Método para remover espaços em branco na coluna especificada
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove espaços em branco
            }
        }
    }

    // Método para expandir o array de dados
    private static String[][] expandArray(String[][] original, int newLength) {
        String[][] newArray = new String[newLength][];
        System.arraycopy(original, 0, newArray, 0, original.length);
        return newArray;
    }

    // Método para escrever os dados no arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            for (String[] row : data) {
                printer.printRecord(Arrays.asList(row));
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}
