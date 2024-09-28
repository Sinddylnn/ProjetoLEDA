package main.java.ordenacao_trending_full_date.melhorCaso;

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

public class QuickSortMedianaMC3 {

    public static void main(String[] args) {
        // Marca o tempo de início da execução para medir o desempenho
        long startTime = System.currentTimeMillis();

        // Caminhos de entrada (CSV original) e saída (CSV ordenado)
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_quickSortMediana_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_trending_full_date_quickSortMediana_melhorCaso.csv");
        int dateIndex = 2; // Índice da coluna "trending_full_date"

        // Informações sobre o início da leitura do arquivo
        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return;
        }

        // Lê o CSV e armazena os dados em uma matriz bidimensional (array)
        String[][] data = readCsv(inputPath);

        // Se o arquivo não estiver vazio, começa a ordenação
        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, dateIndex);  // Remove espaços desnecessários na coluna

            // Mostra os valores da coluna antes da ordenação para fins de depuração
            System.out.println("Valores da coluna trending_full_date antes da ordenação:");
            for (int i = 1; i < data.length; i++) {
                System.out.println(data[i][dateIndex]);
            }

            // Verifica se os dados já estão ordenados em ordem decrescente
            if (isAlreadySorted(data, dateIndex) && "14/06/2018".equals(data[1][dateIndex])) {
                System.out.println("Os dados já estão ordenados e o primeiro item é '14/06/2018'. Pulando a fase de ordenação.");
            } else {
                // Se não estiverem ordenados, realiza a ordenação
                System.out.println("Iniciando ordenação por trending_full_date em ordem decrescente...");
                quickSort(data, 1, data.length - 1, dateIndex); // Ordena em ordem decrescente
            }

            // Salva os dados (ordenados ou não) no arquivo de saída
            writeCsv(data, outputPath);
            System.out.println("Arquivo salvo em: " + outputPath.toAbsolutePath());
        } else {
            System.err.println("Dados insuficientes para ordenar ou o arquivo está vazio.");
        }

        // Medições de desempenho e uso de memória
        long endTime = System.currentTimeMillis();
        Runtime runtime = Runtime.getRuntime();
        long memoryUsed = runtime.totalMemory() - runtime.freeMemory();

        System.out.println("Tempo de execução: " + (endTime - startTime) + " ms");
        System.out.println("Memória utilizada: " + (memoryUsed / (1024 * 1024)) + " MB");
    }

    // Função para verificar se os dados já estão em ordem decrescente
    public static boolean isAlreadySorted(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length - 1; i++) {
            if (compareDates(data[i][columnIndex], data[i + 1][columnIndex]) < 0) {
                return false; // Se encontrar uma data fora de ordem decrescente, retorna falso
            }
        }
        return true; // Se passar pelo loop, já está ordenado
    }

    // Função que implementa o algoritmo QuickSort
    public static void quickSort(String[][] data, int low, int high, int columnIndex) {
        // Pilha usada para eliminar recursão
        int[] stack = new int[high - low + 1];
        int top = -1;

        // Empilha os limites iniciais
        stack[++top] = low;
        stack[++top] = high;

        // Enquanto houver elementos na pilha
        while (top >= 0) {
            high = stack[top--];
            low = stack[top--];

            // Particiona os dados
            int pi = partition(data, low, high, columnIndex);

            // Se há elementos à esquerda da partição, empilha o índice esquerdo
            if (pi - 1 > low) {
                stack[++top] = low;
                stack[++top] = pi - 1;
            }

            // Se há elementos à direita da partição, empilha o índice direito
            if (pi + 1 < high) {
                stack[++top] = pi + 1;
                stack[++top] = high;
            }
        }
    }

    // Função que realiza a partição no QuickSort, usando mediana de três
    public static int partition(String[][] data, int low, int high, int columnIndex) {
        int mid = low + (high - low) / 2;

        // Ordena os valores low, mid, high para achar a mediana
        if (compareDates(data[low][columnIndex], data[mid][columnIndex]) > 0) {
            swap(data, low, mid);
        }
        if (compareDates(data[low][columnIndex], data[high][columnIndex]) > 0) {
            swap(data, low, high);
        }
        if (compareDates(data[mid][columnIndex], data[high][columnIndex]) > 0) {
            swap(data, mid, high);
        }

        // Coloca a mediana como pivô
        swap(data, mid, high - 1);
        String pivot = data[high - 1][columnIndex];
        int i = low;

        for (int j = low; j < high - 1; j++) {
            // Compara para garantir a ordem decrescente
            if (compareDates(data[j][columnIndex], pivot) > 0) {
                swap(data, i++, j);
            }
        }

        // Coloca o pivô no lugar correto
        swap(data, i, high - 1);
        return i;
    }

    // Função de troca de posições entre dois elementos
    private static void swap(String[][] data, int i, int j) {
        String[] temp = data[i];
        data[i] = data[j];
        data[j] = temp;
    }

    // Função para comparar duas datas no formato dd/MM/yyyy
    public static int compareDates(String date1, String date2) {
        String[] parts1 = date1.split("/");
        String[] parts2 = date2.split("/");

        int yearComparison = Integer.compare(Integer.parseInt(parts1[2]), Integer.parseInt(parts2[2]));
        if (yearComparison != 0) return yearComparison;

        int monthComparison = Integer.compare(Integer.parseInt(parts1[1]), Integer.parseInt(parts2[1]));
        if (monthComparison != 0) return monthComparison;

        return Integer.compare(Integer.parseInt(parts1[0]), Integer.parseInt(parts2[0]));
    }

    // Função que lê o CSV e retorna os dados em um array bidimensional
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

            // Lê as linhas do CSV e armazena no array bidimensional
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
        }
        return data;
    }

    // Função para expandir dinamicamente o array
    private static String[][] expandArray(String[][] data, int newLength) {
        String[][] newArray = new String[newLength][data[0].length];
        System.arraycopy(data, 0, newArray, 0, data.length);
        return newArray;
    }

    // Função que grava os dados no arquivo CSV
    public static void writeCsv(String[][] data, Path outputPath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(outputPath.toFile()), CSVFormat.DEFAULT)) {
            for (String[] row : data) {
                printer.printRecord((Object[]) row);
            }
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo: " + e.getMessage());
        }
    }

    // Função para limpar espaços em branco no início dos dados em uma coluna
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length; i++) {
            data[i][columnIndex] = data[i][columnIndex].trim();
        }
    }
}
