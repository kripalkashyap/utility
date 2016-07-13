package com.lectorl.util.excel;

import com.lectorl.util.excel.exception.CellValueConvertException;
import com.lectorl.util.excel.exception.ModelNotFoundException;
import com.lectorl.util.excel.model.Book;
import com.lectorl.util.excel.model.Person;
import org.apache.poi.hssf.usermodel.HSSFSheet;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.junit.Assert;
import org.junit.Before;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.junit.runners.JUnit4;

import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.Date;

/**
 * Created by Reza Mousavi reza.mousavi@lector.dk on 7/8/2016
 */
@RunWith(JUnit4.class)
public class ExcelManipulationConfigurationTest {

    public static final String TEST_XLS = "test.xls";

    private CellConverter cellConverter;

    @Before
    public void setUp() throws Exception {
        cellConverter = new CellConverter();
    }

    @Test
    public void testCreatedRowIsEmpty() throws IOException {
        ExcelManipulationConfiguration configuration = new ExcelManipulationConfiguration();
        configuration.addModel(Book.class);
        final Book book = new Book();
        final HSSFSheet sheet = getSheet();
        final Row row = configuration.toRow(book, sheet);
        Assert.assertNotEquals(null, row);

        for (Cell cell : row) {
            Assert.assertEquals(null, cell);
        }
    }

    @Test
    public void testCreatedRowIsNotEmpty() throws IOException {
        ExcelManipulationConfiguration configuration = new ExcelManipulationConfiguration();
        configuration.addModel(Book.class);
        final Book book = getSampleBook();
        final HSSFSheet sheet = getSheet();
        final Row row = configuration.toRow(book, sheet);
        Assert.assertNotEquals(null, row);

        Assert.assertEquals("Title is null", false, cellConverter.toJava(row, 1, String.class).isPresent());
        Assert.assertEquals("Title field equality", book.getAuthor(), cellConverter.toJava(row, 2, String.class).get());
        Assert.assertEquals("Title field equality", book.getIsbn(), cellConverter.toJava(row, 9, String.class).get());
        Assert.assertEquals("Title field equality", book.getLanguage(), cellConverter.toJava(row, 6, String.class).get());
        Assert.assertEquals("Title field equality", book.getPrice(), cellConverter.toJava(row, 3, BigDecimal.class).get());
        Assert.assertEquals("Title field equality", book.getReleaseDate(), cellConverter.toJava(row, 4, LocalDate.class).get());
        Assert.assertEquals("Title field equality", false, cellConverter.toJava(row, 5, String.class).isPresent());
    }

    @Test(expected = ModelNotFoundException.class)
    public void testEmptyModel() throws IOException {
        final ExcelManipulationConfiguration configuration = new ExcelManipulationConfiguration();
        final HSSFSheet sheet = getSheet();
        final Row row = configuration.toRow(new Book(), sheet);
    }

    @Test(expected = ModelNotFoundException.class)
    public void testUnrelatedModel() throws IOException {
        final ExcelManipulationConfiguration configuration = new ExcelManipulationConfiguration();
        configuration.addModel(Person.class);
        final HSSFSheet sheet = getSheet();
        final Row row = configuration.toRow(new Book(), sheet);
    }
    @Test
    public void testCreatedRowStringCellValue() throws IOException {
        final ExcelManipulationConfiguration configuration = new ExcelManipulationConfiguration();
        configuration.addModel(Book.class);
        final Book book = new Book();
        final String title = "Reza";
        book.setTitle(title);
        final HSSFSheet sheet = getSheet();
        final Row row = configuration.toRow(book, sheet);
        Assert.assertNotEquals(null, row);

        Assert.assertNotEquals(null, row.getCell(1));
        Assert.assertEquals(title, cellConverter.toJava(row, 1, String.class).get());
    }

    @Test
    public void testCreatedRowLocalDateCellValue() throws IOException {
        final ExcelManipulationConfiguration configuration = new ExcelManipulationConfiguration();
        configuration.addModel(Book.class);
        final Book book = new Book();
        final LocalDate now = LocalDate.now();
        book.setReleaseDate(now);
        final HSSFSheet sheet = getSheet();
        final Row row = configuration.toRow(book, sheet);
        Assert.assertNotEquals(null, row);

        Assert.assertNotEquals(null, row.getCell(4));
        Assert.assertEquals(now, cellConverter.toJava(row, 4, LocalDate.class).get());
    }

    @Test(expected = CellValueConvertException.class)
    public void testCreatedRowStringIncompatibleCellValue() throws IOException {
        final ExcelManipulationConfiguration configuration = new ExcelManipulationConfiguration();
        configuration.addModel(Book.class);
        final Book book = new Book();
        final String title = "Reza";
        book.setTitle(title);
        final HSSFSheet sheet = getSheet();
        final Row row = configuration.toRow(book, sheet);
        Assert.assertNotEquals(null, row);

        Assert.assertNotEquals(null, row.getCell(1));
        Assert.assertEquals(title, cellConverter.toJava(row, 1, String.class).get());
        Assert.assertNotEquals("Cell value", cellConverter.toJava(row, 1, Date.class));
    }

    private Book getSampleBook() {
        Book book = new Book();
        book.setAuthor("Reza Mousavi");
        book.setIsbn("1235432");
        book.setLanguage("Persian");
        book.setPrice(new BigDecimal(12.5));
        book.setPublisher("Cambridge");
        book.setReleaseDate(LocalDate.now());
        //book.setTitle("A guide for java lovers");
        return book;
    }

    private HSSFSheet getSheet() throws IOException {
        final HSSFWorkbook workbook = new HSSFWorkbook();
        return workbook.createSheet();
    }

}
