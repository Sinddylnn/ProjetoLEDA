package main.java.ordenacao_channel_title.piorCaso;
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
public class HeapSortPC {
    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Caminhos de entrada e saída ajustados
        Path inputPath = Paths.get("Diretório do",  "videos_T1_channel_title_heapSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do",  "videos_T1_channel_title_heapSort_piorCaso.csv");
        int columnIndex = 4; // Índice da coluna "channel_title"

        System.out.println("Iniciando leitura do arquivo CSV: " + inputPath.toString());
        System.out.println("Caminho absoluto: " + inputPath.toAbsolutePath());

        // Verifica se o arquivo existe antes de tentar ler
        if (!Files.exists(inputPath)) {
            System.err.println("Arquivo não encontrado: " + inputPath.toAbsolutePath());
            return; // Encerra o programa se o arquivo não for encontrado
        }

        // Carregar dados do CSV
        String[][] data = readCsv(inputPath);

        if (data != null && data.length > 1) {
            System.out.println("Arquivo lido. Iniciando limpeza dos espaços iniciais...");
            cleanSpacesInColumn(data, columnIndex); // Limpa espaços da coluna especificada

            System.out.println("Iniciando preenchimento das linhas com menos colunas...");
            fillMissingColumns(data); // Preenche colunas faltantes em linhas com menos colunas

            // Verifica se já está ordenado
            if (isAlreadySorted(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação...");
                try {
                    sortByChannelTitleHeapSort(data, columnIndex); // Ordena os dados usando HeapSort
                    System.out.println("Ordenação finalizada. Salvando arquivo...");
                } catch (IndexOutOfBoundsException e) {
                    System.err.println("Erro ao ordenar: " + e.getMessage());
                    e.printStackTrace(); // Exibe o erro caso ocorra uma exceção
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

    // Função de ordenação HeapSort
    public static void sortByChannelTitleHeapSort(String[][] data, int columnIndex) {
        if (data.length <= 1) {
            System.err.println("Lista de dados está vazia ou contém apenas o cabeçalho.");
            return; // Encerra se não houver dados para ordenar
        }

        if (columnIndex < 0 || data[0].length <= columnIndex) {
            System.err.println("Índice de coluna inválido: " + columnIndex);
            return; // Verifica se o índice da coluna é válido
        }

        String[] header = data[0]; // Armazena o cabeçalho
        String[][] dataToSort = Arrays.copyOfRange(data, 1, data.length); // Copia os dados para ordenar

        System.out.println("Iniciando HeapSort para a coluna: " + columnIndex);

        // Construir o heap (reorganizar o array)
        int n = dataToSort.length;
        for (int i = n / 2 - 1; i >= 0; i--) {
            heapify(dataToSort, n, i, columnIndex); // Cria o heap
        }

        // Extrair elementos do heap um por um
        for (int i = n - 1; i >= 0; i--) {
            // Move a raiz atual para o final
            String[] temp = dataToSort[0];
            dataToSort[0] = dataToSort[i];
            dataToSort[i] = temp;
            System.out.println("Elemento na posição " + i + " trocado com a raiz.");

            // Chama heapify no heap reduzido
            heapify(dataToSort, i, 0, columnIndex);
        }

        // Recolocar o cabeçalho
        String[][] sortedData = new String[data.length][]; // Cria um novo array para armazenar os dados ordenados
        sortedData[0] = header; // Define o cabeçalho
        System.arraycopy(dataToSort, 0, sortedData, 1, dataToSort.length); // Copia os dados ordenados
        System.arraycopy(sortedData, 0, data, 0, sortedData.length); // Atualiza o array original com os dados ordenados

        System.out.println("Ordenação HeapSort concluída.");
    }

    // Função auxiliar para organizar o heap
    private static void heapify(String[][] data, int n, int i, int columnIndex) {
        int largest = i; // Inicializa o maior como a raiz
        int left = 2 * i + 1; // Filho à esquerda
        int right = 2 * i + 2; // Filho à direita

        // Se o filho à esquerda for maior que a raiz
        if (left < n && data[left][columnIndex].trim().compareToIgnoreCase(data[largest][columnIndex].trim()) > 0) {
            largest = left; // Atualiza o maior
            System.out.println("Filho à esquerda " + Arrays.toString(data[left]) + " é maior que " + Arrays.toString(data[i]));
        }

        // Se o filho à direita for maior que o maior até agora
        if (right < n && data[right][columnIndex].trim().compareToIgnoreCase(data[largest][columnIndex].trim()) > 0) {
            largest = right; // Atualiza o maior
            System.out.println("Filho à direita " + Arrays.toString(data[right]) + " é maior que " + Arrays.toString(data[largest]));
        }

        // Se o maior não é a raiz
        if (largest != i) {
            String[] swap = data[i]; // Troca os valores
            data[i] = data[largest];
            data[largest] = swap;

            System.out.println("Trocando " + Arrays.toString(data[i]) + " com " + Arrays.toString(data[largest]));

            // Recursivamente transforma a subárvore afetada em um heap
            heapify(data, n, largest, columnIndex);
        }
    }

    // Função para verificar se os dados já estão ordenados em ordem decrescente
    public static boolean isAlreadySorted(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length; i++) { // Começar de 1
            String[] previousRow = data[i - 1];
            String[] currentRow = data[i];

            if (previousRow.length <= columnIndex || currentRow.length <= columnIndex) {
                continue; // Ignora se a linha não tem a coluna necessária
            }

            String previousValue = previousRow[columnIndex].trim();
            String currentValue = currentRow[columnIndex].trim();

            if (previousValue.isEmpty() || currentValue.isEmpty()) {
                continue; // Ignora linhas vazias
            }

            // Verifica se o valor anterior é menor que o atual
            if (previousValue.compareToIgnoreCase(currentValue) < 0) {
                return false; // Não está ordenado
            }
        }
        return true; // Se passar por todas as comparações, está ordenado
    }

    // Função para ler dados de um arquivo CSV
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][]; // Inicializa um array vazio
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê a primeira linha como cabeçalho
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(","); // Separa os cabeçalhos por vírgula
            data = new String[1][headers.length]; // Cria um array para armazenar os dados
            data[0] = headers; // Armazena os cabeçalhos

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim() // Remove espaços em branco
                    .withQuoteMode(QuoteMode.ALL); // Configura para lidar com aspas

            // Lê registros CSV
            try (CSVParser parser = new CSVParser(reader, csvFormat)) {
                for (CSVRecord record : parser) {
                    String[] row = new String[headers.length]; // Cria um array para a linha atual
                    for (int i = 0; i < headers.length; i++) {
                        row[i] = i < record.size() ? record.get(i) : ""; // Preenche as colunas
                    }
                    data = Arrays.copyOf(data, data.length + 1); // Expande o array
                    data[data.length - 1] = row; // Adiciona a nova linha
                }
            }
        } catch (IOException e) {
            System.err.println("Erro ao ler o arquivo: " + e.getMessage());
        }
        return data; // Retorna os dados lidos
    }

    // Função para escrever dados em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT.withHeader(data[0]))) {
            for (int i = 1; i < data.length; i++) {
                printer.printRecord((Object[]) data[i]); // Escreve cada linha no arquivo CSV
            }
            printer.flush(); // Garante que os dados sejam escritos
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo: " + e.getMessage());
        }
    }

    // Função para limpar espaços iniciais na coluna especificada
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        for (int i = 1; i < data.length; i++) { // Ignora a primeira linha (cabeçalho)
            if (data[i].length > columnIndex) {
                data[i][columnIndex] = data[i][columnIndex].trim(); // Remove espaços iniciais
            }
        }
    }

    // Função para preencher colunas faltantes em linhas com menos colunas
    public static void fillMissingColumns(String[][] data) {
        int maxColumns = Arrays.stream(data).mapToInt(arr -> arr.length).max().orElse(0); // Obtém o número máximo de colunas
        for (int i = 0; i < data.length; i++) {
            if (data[i].length < maxColumns) {
                String[] newRow = Arrays.copyOf(data[i], maxColumns); // Cria um novo array para a linha
                Arrays.fill(newRow, data[i].length, maxColumns, ""); // Preenche as colunas faltantes com vazios
                data[i] = newRow; // Atualiza a linha
            }
        }
    }
}
