package com.apple.allocator.helper;

import java.io.BufferedReader;
import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.PrintWriter;
import java.math.BigInteger;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;
import java.util.List;

import com.apple.allocator.model.DemandOrder;
import com.apple.allocator.model.Plan;
import com.apple.allocator.model.SourcingRule;
import com.apple.allocator.model.Supply;
import org.apache.commons.csv.CSVFormat;
import org.apache.commons.csv.CSVParser;
import org.apache.commons.csv.CSVPrinter;
import org.apache.commons.csv.CSVRecord;
import org.apache.commons.csv.QuoteMode;
import org.springframework.web.multipart.MultipartFile;


public class CSVHelper {
    public static String TYPE = "text/csv";
    static String[] HEADERs = { "Id", "Title", "Description", "Published" };

    public static boolean hasCSVFormat(MultipartFile file) {

        if (!TYPE.equals(file.getContentType())) {
            return false;
        }

        return true;
    }

    public static List<SourcingRule> csvToSourcingRules(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<SourcingRule> sourcingRules = new ArrayList<SourcingRule>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                SourcingRule sourcingRule = new SourcingRule(
                        csvRecord.get("site"),
                        csvRecord.get("customer"),
                        csvRecord.get("product")
                );

                sourcingRules.add(sourcingRule);
            }

            return sourcingRules;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        }
    }

    public static List<DemandOrder> csvToDemandOrders(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<DemandOrder> demandOrders = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                Date parsed = format.parse(csvRecord.get("date"));
                java.sql.Date date = new java.sql.Date(parsed.getTime());

                DemandOrder demandOrder = new DemandOrder(
                        csvRecord.get("customer"),
                        csvRecord.get("product"),
                        date,
                        new BigInteger(csvRecord.get("quantity"))
                );

                demandOrders.add(demandOrder);
            }

            return demandOrders;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        } catch (java.text.ParseException e) {
            throw new RuntimeException("fail to parse CSV file: date format not correct" + e.getMessage());
        }
    }

    public static List<Supply> csvToSupplies(InputStream is) {
        try (BufferedReader fileReader = new BufferedReader(new InputStreamReader(is, "UTF-8"));
             CSVParser csvParser = new CSVParser(fileReader,
                     CSVFormat.DEFAULT.withFirstRecordAsHeader().withIgnoreHeaderCase().withTrim());) {

            List<Supply> supplies = new ArrayList<>();

            Iterable<CSVRecord> csvRecords = csvParser.getRecords();

            for (CSVRecord csvRecord : csvRecords) {
                SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");
                Date parsed = format.parse(csvRecord.get("date"));
                java.sql.Date date = new java.sql.Date(parsed.getTime());

                Supply supply = new Supply(
                        csvRecord.get("site"),
                        csvRecord.get("product"),
                        date,
                        new BigInteger(csvRecord.get("quantity"))
                );

                supplies.add(supply);
            }

            return supplies;
        } catch (IOException e) {
            throw new RuntimeException("fail to parse CSV file: " + e.getMessage());
        } catch (java.text.ParseException e) {
            throw new RuntimeException("fail to parse CSV file: date format not correct" + e.getMessage());
        }
    }

    public static ByteArrayInputStream plansToCSV(List<Plan> plans) {
        final CSVFormat format = CSVFormat.DEFAULT.withQuoteMode(QuoteMode.MINIMAL);

        try (ByteArrayOutputStream out = new ByteArrayOutputStream();
             CSVPrinter csvPrinter = new CSVPrinter(new PrintWriter(out), format);) {
            for (Plan plan : plans) {
                List<String> data = Arrays.asList(
                        plan.getDate().toString(),
                        plan.getSite(),
                        plan.getCustomer(),
                        plan.getProduct(),
                        String.valueOf(plan.getQuantity())
                );

                csvPrinter.printRecord(data);
            }

            csvPrinter.flush();
            return new ByteArrayInputStream(out.toByteArray());
        } catch (IOException e) {
            throw new RuntimeException("fail to import data to CSV file: " + e.getMessage());
        }
    }

}

