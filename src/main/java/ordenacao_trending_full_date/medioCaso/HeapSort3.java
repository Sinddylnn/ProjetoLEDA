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

public class HeapSort3 {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Define os caminhos de entrada e saída do arquivo CSV
        Path inputPath = Paths.get("Diretório do csv", "videos_T1.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_heapSort_medioCaso.csv");
        int dateIndex = 2; // Índice da coluna "trending_full_date"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo de entrada existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Lê os dados do arquivo CSV
        String[][] data = readCsv(inputPath);

        // Verifica se há dados suficientes para ordenar
        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, dateIndex); // Limpa espaços em branco na coluna de datas

            System.out.println("Valores da coluna trending_full_date antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Verifica se os dados já estão ordenados de forma decrescente
            if (isSortedDescending(data, dateIndex)) {
                System.out.println("O arquivo já está ordenado em ordem decrescente.");
            } else {
                System.out.println("Iniciando ordenação por trending_full_date em ordem decrescente...");
                heapSort(data, dateIndex); // Realiza a ordenação usando HeapSort
            }

            writeCsv(data, outputPath); // Grava os dados ordenados em um novo arquivo CSV
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

    // Método para realizar o HeapSort por data em ordem decrescente
    public static void heapSort(String[][] data, int columnIndex) {
        System.out.println("Executando HeapSort por trending_full_date na coluna: " + columnIndex);
        int n = data.length;

        // Construindo o heap (reorganiza o array)
        for (int i = n / 2 - 1; i >= 1; i--) {
            heapify(data, n, i, columnIndex);
        }

        // Extrai um elemento do heap um por um
        for (int i = n - 1; i >= 1; i--) {
            // Move a raiz atual para o final
            String[] temp = data[i];
            data[i] = data[1]; // Move a raiz para o final
            data[1] = temp;

            // Chama o heapify na heap reduzida
            heapify(data, i, 1, columnIndex);
        }

        System.out.println("Ordenação concluída. Dados ordenados por trending_full_date:");
        for (int i = 1; i < data.length; i++) {
            System.out.println(data[i][columnIndex]);
        }
    }

    // Método para converter o array em um max heap
    private static void heapify(String[][] data, int n, int i, int columnIndex) {
        int largest = i; // Inicializa largest como root
        int left = 2 * i; // left = 2*i
        int right = 2 * i + 1; // right = 2*i + 1

        // Compara o filho esquerdo com a raiz
        if (left < n && compareDates(data[left][columnIndex], data[largest][columnIndex]) > 0) {
            largest = left;
        }

        // Compara o filho direito com o maior até agora
        if (right < n && compareDates(data[right][columnIndex], data[largest][columnIndex]) > 0) {
            largest = right;
        }

        // Se o maior não for a raiz
        if (largest != i) {
            String[] swap = data[i];
            data[i] = data[largest];
            data[largest] = swap;

            // Recursivamente heapify o sub-árvore afetado
            heapify(data, n, largest, columnIndex);
        }
    }

    // Método para comparar duas datas
    private static int compareDates(String date1, String date2) {
        String[] date1Parts = date1.split("/");
        String[] date2Parts = date2.split("/");

        int year1 = Integer.parseInt(date1Parts[2]);
        int month1 = Integer.parseInt(date1Parts[1]);
        int day1 = Integer.parseInt(date1Parts[0]);

        int year2 = Integer.parseInt(date2Parts[2]);
        int month2 = Integer.parseInt(date2Parts[1]);
        int day2 = Integer.parseInt(date2Parts[0]);

        // Comparar primeiro por ano, depois por mês, depois por dia
        if (year1 != year2) {
            return Integer.compare(year2, year1); // Decrescente
        } else if (month1 != month2) {
            return Integer.compare(month2, month1); // Decrescente
        } else {
            return Integer.compare(day2, day1); // Decrescente
        }
    }

    // Método para ler dados de um arquivo CSV
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

    // Método para limpar espaços em branco na coluna especificada
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Limpando espaços nas colunas...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = row[columnIndex].trim(); // Remove espaços em branco
            }
        }
    }

    // Método para expandir o array
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

    // Método para verificar se os dados estão ordenados em ordem decrescente
    public static boolean isSortedDescending(String[][] data, int columnIndex) {
        for (int i = 2; i < data.length; i++) { // Começa do índice 2 para ignorar o cabeçalho
            if (compareDates(data[i][columnIndex], data[i - 1][columnIndex]) > 0) {
                return false; // Se encontrar uma ordem ascendente, retorna false
            }
        }
        return true; // Retorna true se estiver ordenado em ordem decrescente
    }
}
