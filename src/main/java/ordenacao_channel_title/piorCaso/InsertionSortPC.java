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
public class InsertionSortPC {


    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();

        // Caminhos de entrada e saída ajustados
        Path inputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_insertionSort_medioCaso.csv");
        Path outputPath = Paths.get("Diretório do csv", "videos_T1_channel_title_insertionSort_piorCaso.csv");
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

            // Verifica se já está ordenado em ordem decrescente
            if (isAlreadySortedDescending(data, columnIndex)) {
                System.out.println("Os dados já estão ordenados em ordem decrescente. Pulando a ordenação.");
            } else {
                System.out.println("Iniciando ordenação decrescente...");
                try {
                    sortByChannelTitleDescending(data, columnIndex);
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

    // Verifica se os dados já estão em ordem decrescente
    public static boolean isAlreadySortedDescending(String[][] data, int columnIndex) {
        for (int i = 2; i < data.length; i++) {
            String[] previousRow = data[i - 1];
            String[] currentRow = data[i];

            if (previousRow.length <= columnIndex || currentRow.length <= columnIndex) {
                continue; // Ignora se a linha não tiver colunas suficientes
            }

            String previousValue = previousRow[columnIndex].trim();
            String currentValue = currentRow[columnIndex].trim();

            if (previousValue.isEmpty() || currentValue.isEmpty()) {
                continue; // Ignora se alguma das células estiver vazia
            }

            // Compara os valores para verificar se estão em ordem decrescente
            if (previousValue.compareToIgnoreCase(currentValue) < 0) {
                return false; // Retorna false se encontrar uma violação da ordem decrescente
            }
        }
        return true; // Retorna true se todos os pares de linhas estiverem em ordem decrescente
    }

    // Lê o arquivo CSV e armazena os dados em um array bidimensional
    public static String[][] readCsv(Path filePath) {
        String[][] data = new String[0][]; // Inicializa o array de dados
        try (BufferedReader reader = new BufferedReader(new FileReader(filePath.toFile()))) {
            String headerLine = reader.readLine(); // Lê a primeira linha (cabeçalho)
            if (headerLine == null) {
                throw new IllegalArgumentException("Arquivo CSV vazio ou cabeçalho ausente.");
            }

            String[] headers = headerLine.split(","); // Divide os cabeçalhos por vírgula
            data = new String[1][headers.length]; // Inicializa o array com o cabeçalho
            data[0] = headers;

            System.out.println("Cabeçalhos encontrados: " + Arrays.toString(headers));

            CSVFormat csvFormat = CSVFormat.DEFAULT
                    .withTrim()
                    .withQuoteMode(QuoteMode.ALL);

            CSVParser csvParser = new CSVParser(reader, csvFormat);
            int rowCount = 1; // Contador de linhas
            for (CSVRecord record : csvParser) {
                if (record.size() < headers.length) {
                    System.out.println("Linha ignorada por não ter colunas suficientes.");
                    continue; // Ignora linhas que não têm colunas suficientes
                }
                data = expandArray(data, rowCount + 1); // Expande o array para adicionar nova linha
                data[rowCount] = record.stream().toArray(String[]::new); // Adiciona a linha ao array de dados
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
        return data; // Retorna os dados lidos do CSV
    }

    // Expande um array bidimensional
    public static String[][] expandArray(String[][] original, int newSize) {
        String[][] newArray = new String[newSize][]; // Cria um novo array com o novo tamanho
        System.arraycopy(original, 0, newArray, 0, original.length); // Copia os dados do array original
        return newArray; // Retorna o novo array expandido
    }

    // Limpa os espaços em branco das células de uma coluna específica
    public static void cleanSpacesInColumn(String[][] data, int columnIndex) {
        System.out.println("Removendo espaços iniciais na coluna " + columnIndex + "...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length > columnIndex) {
                row[columnIndex] = removeLeadingAndTrailingSpaces(row[columnIndex]); // Remove espaços em branco
                System.out.println("Linha " + i + " após limpeza: " + Arrays.toString(row));
            }
        }
    }

    // Remove espaços em branco do início e do fim de uma string
    private static String removeLeadingAndTrailingSpaces(String input) {
        return input.replaceAll("^\\s+", "").replaceAll("\\s+$", ""); // Usa expressões regulares para remover espaços
    }

    // Preenche as linhas que têm menos colunas com strings vazias
    public static void fillMissingColumns(String[][] data) {
        String[] headers = data[0]; // Obtém os cabeçalhos
        int numColumns = headers.length; // Contagem do número de colunas

        System.out.println("Preenchendo colunas ausentes...");
        for (int i = 1; i < data.length; i++) {
            String[] row = data[i];
            if (row.length < numColumns) {
                String[] newRow = new String[numColumns]; // Cria uma nova linha com o tamanho correto
                System.arraycopy(row, 0, newRow, 0, row.length); // Copia os dados existentes
                for (int j = row.length; j < numColumns; j++) {
                    newRow[j] = ""; // Preenche colunas ausentes com strings vazias
                }
                data[i] = newRow; // Atualiza a linha no array de dados
                System.out.println("Linha " + i + " após preenchimento: " + Arrays.toString(newRow));
            }
        }
    }

    // Ordenação decrescente pelo título do canal
    public static void sortByChannelTitleDescending(String[][] data, int columnIndex) {
        if (data.length <= 1) {
            System.err.println("Lista de dados está vazia ou contém apenas o cabeçalho.");
            return;
        }

        if (columnIndex < 0 || data[0].length <= columnIndex) {
            System.err.println("Índice da coluna fora dos limites: " + columnIndex);
            return;
        }

        System.out.println("Iniciando ordenação...");
        for (int i = 2; i < data.length; i++) {
            String[] key = data[i];
            String keyValue = key[columnIndex].trim(); // Obtém o valor da coluna chave
            int j = i - 1;

            // Move elementos maiores que key para uma posição à frente
            while (j >= 2 && data[j][columnIndex].trim().compareToIgnoreCase(keyValue) < 0) {
                data[j + 1] = data[j];
                j--;
            }
            data[j + 1] = key; // Insere o key na posição correta
            System.out.println("Estado após a inserção da linha " + i + ": " + Arrays.toString(data[j + 1]));
        }
    }

    // Escreve os dados ordenados de volta em um arquivo CSV
    public static void writeCsv(String[][] data, Path filePath) {
        try (CSVPrinter printer = new CSVPrinter(new FileWriter(filePath.toFile()), CSVFormat.DEFAULT
                .withHeader(data[0]).withQuoteMode(QuoteMode.ALL))) {
            for (int i = 1; i < data.length; i++) {
                printer.printRecord(Arrays.asList(data[i])); // Imprime as linhas no arquivo CSV
                System.out.println("Linha escrita: " + Arrays.toString(data[i]));
            }
            System.out.println("Arquivo salvo em: " + filePath.toAbsolutePath());
        } catch (IOException e) {
            System.err.println("Erro ao escrever o arquivo: " + e.getMessage());
            e.printStackTrace();
        }
    }
}

