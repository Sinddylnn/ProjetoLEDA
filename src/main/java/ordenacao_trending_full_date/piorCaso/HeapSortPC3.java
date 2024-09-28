package main.java.ordenacao_trending_full_date.piorCaso;

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

public class HeapSortPC3 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        Path inputPath = Paths.get("Diretório do csv",  "videos_T1_trending_full_date_heapSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_heapSort_piorCaso.csv");
        int dateIndex = 2; // Índice da coluna "trending_full_date"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, dateIndex);

            System.out.println("Valores da coluna trending_full_date antes da inversão:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Verifica se os dados estão ordenados em ordem crescente
            if (isSortedAscending(data, dateIndex)) {
                System.out.println("Os dados estão ordenados em ordem crescente.");
            } else {
                System.out.println("Os dados não estão ordenados em ordem crescente.");
            }

            // Inversão da ordem dos dados
            reverseArray(data);

            System.out.println("Valores da coluna trending_full_date após a inversão:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            writeCsv(data, outputPath);
            System.out.println("Arquivo invertido salvo em: " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Método para inverter a ordem dos dados
    public static void reverseArray(String[][] data) {
        for (int i = 1, j = data.length - 1; i < j; i++, j--) {
            String[] temp = data[i];
            data[i] = data[j];
            data[j] = temp;
        }
    }

    // Método para ler o arquivo CSV e retornar um array bidimensional
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][]; // Inicializa o array de dados
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê o cabeçalho do arquivo
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(","); // Divide o cabeçalho em colunas
            data = new String[1][headers.length];
            data[0] = headers;

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim() // Remove espaços em branco
                    .withQuoteMode(QuoteMode.ALL); // Define o modo de citação

            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1; // Contador de linhas
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue;
                }
                data = expandArray(data, rowCount + 1); // Expande o array de dados
                data[rowCount] = record.stream().toArray(String[]::new); // Adiciona a linha ao array
                rowCount++;
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            System.err.println("Erro de argumento: " + e.getMessage());
        }
        return data; // Retorna o array de dados lidos
    }

    // Método para limpar espaços em branco em uma coluna específica
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove espaços em branco
            }
        }
    }

    // Método para expandir um array bidimensional
    private static String[][] expandArray(String[][] original, int newLength) {
        String[][] newArray = new String[newLength][]; // Cria um novo array
        System.arraycopy(original, 0, newArray, 0, original.length); // Copia os dados do array original
        return newArray; // Retorna o novo array
    }

    // Método para escrever o array em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withQuoteMode(QuoteMode.ALL))) {
            for (String[] row : data) {
                printer.printRecord(row); // Escreve cada linha no arquivo
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever no arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }

    // Método para verificar se os dados estão ordenados em ordem crescente
    public static boolean isSortedAscending(String[][] data, int columnIndex) {
        for (int i = 2; i < data.length; i++) { // Começa do índice 2 para ignorar o cabeçalho
            if (data[i][columnIndex].compareTo(data[i - 1][columnIndex]) < 0) {
                return false; // Se encontrar uma ordem descendente, retorna false
            }
        }
        return true; // Retorna true se estiver ordenado em ordem crescente
    }
}
