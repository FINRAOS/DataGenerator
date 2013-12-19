/*
 * (C) Copyright 2013 DataGenerator Contributors.
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 *
 */
package org.finra.datagenerator.input;

import com.google.common.base.Preconditions;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;
import org.apache.log4j.Logger;
import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Cell;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;

/**
 * Reads a DataSpec from .xls files. See wiki for documentatation on xls syntax.
 *
 * @author ChamberA
 *
 */
public class XLSDataSpecReader implements IDataSpecReader {

    private static Logger LOG = Logger.getLogger(XLSDataSpecReader.class);

    private DataSpec dataSpec = new DataSpec();

    // keywords used in parsing a xls file
    private static final String VARTYPE_CELL = "vartype"; //label keyword that indicate a new set of vartype definitions
    private static final String GROUP_CELL = "group"; // label keyword that indicates a vartype belongs to a certain group
    private static final String INSTANCES_PER_PARENT_LABEL = "n";
    private static final String UNIQUE_ELEM_LABEL = "unique";
    private static final String PARENT_GROUP_LABEL = "parent";

    /**
     * Creates a dataspec from .xls files.
     */
    public DataSpec readDataSpecFromFiles(Collection<File> files) {
        Preconditions.checkArgument(!files.isEmpty(), "No .xls files to read dataspec from.");

        for (File f : files) {
            LOG.info("DataSpec: " + f.getName());
            processXLSFile(f);
        }

        // now that all group and var specs are in the dataspec, we need to associate vars with groups
        for (VariableSpec varSpec : dataSpec.getAllVariableSpecs()) {
            dataSpec.getGroupSpec(varSpec.getGroup()).addVariableMember(varSpec);
        }

        return dataSpec;
    }

    /**
     * Incorporates the information from a single xls file into the dataspec.
     *
     * @param xlsFile
     */
    private void processXLSFile(File xlsFile) {

        try {
            Workbook workbook = new HSSFWorkbook(new FileInputStream(xlsFile));
            processXLSSheetOne(workbook);
            processXLSSheetTwo(workbook);

        } catch (FileNotFoundException e) {
            LOG.error("Error reading xlsFile", e);
        } catch (IOException e) {
            LOG.error("Error reading xlsFile", e);
        }
    }

    /**
     * The first sheet of a xls file should contain variable definition. This
     * includes at a minimum, a variable name. It may also include group
     * membership and properties + their positive/negative values. See wiki
     * documentation for full info.
     *
     * @param workbook
     */
    private void processXLSSheetOne(Workbook workbook) {

        Sheet sheet1 = workbook.getSheetAt(0);
        Map<Integer, String> columnLabels = new HashMap<Integer, String>();
        for (Row row : sheet1) {

            //a row of column labels is defined by the first column containing VARTYPE_CELL
            Cell firstCell = row.getCell(0);
            if (firstCell == null) {
                continue; // ignore rows without data in the first cell
            }
            if (firstCell.getCellType() == Cell.CELL_TYPE_STRING
                    && firstCell.getStringCellValue().equalsIgnoreCase(VARTYPE_CELL)) {

                columnLabels.clear(); // we are defining a new set of variables
                for (Cell cell : row) {
                    // build a list of column labels
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        columnLabels.put(cell.getColumnIndex(), cell.getStringCellValue());
                    } else {
                        LOG.debug("Skipping xls column label cell (" + cell.getRowIndex() + "," + cell.getColumnIndex() + ") because it has non-string content");
                        continue;
                    }
                }
            } // if it's a variable definition row, then the first cell will be under the column label VARTYPE_CELL
            else {

                String firstColumnLabel = columnLabels.get(firstCell.getColumnIndex());
                if (firstColumnLabel == null) {
                    LOG.debug("Skipping xls row " + row.getRowNum() + " because its first element doesn't match any column label");
                    columnLabels.clear();
                    continue;
                }

                if (firstCell.getCellType() != Cell.CELL_TYPE_STRING
                        || !firstColumnLabel.equalsIgnoreCase(VARTYPE_CELL)) {
                    LOG.debug("Skipping xls row " + row.getRowNum() + " because its first element is not a string in column " + VARTYPE_CELL);
                    columnLabels.clear();
                    continue;
                }

                // build a Variable across the columns
                VariableSpec varspec = new VariableSpec(firstCell.getStringCellValue());
                for (Cell cell : row) {

                    String columnLabel = columnLabels.get(cell.getColumnIndex());
                    if (columnLabel == null) {
                        LOG.debug("Skipping xls cell (" + cell.getRowIndex() + "," + cell.getColumnIndex() + ") because it doesn't fall under any column label");
                        continue;
                    }

                    if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
                        cell.setCellType(Cell.CELL_TYPE_STRING); // attempt to convert non string cells to strings
                    }
                    String cellContent = cell.getStringCellValue();

                    if (columnLabel.equalsIgnoreCase(VARTYPE_CELL)) {
                        continue; //ignore first cell because we already have a vartype
                    }
                    if (columnLabel.equalsIgnoreCase(GROUP_CELL)) {
                        varspec.setGroup(cellContent); // only allowing membership to a single group for now
                    } else { //other columns are simply properties
                        PropertySpec propertySpec = new PropertySpec(columnLabel);

                        // if it it' enclosed with [ ], remove them and split it on ],[
                        if (cellContent.startsWith("[") && cellContent.endsWith("]")) {
                            cellContent = cellContent.substring(1, cellContent.length() - 1);
                            String[] all_vals = cellContent.split("]\\s*,\\s*\\["); // brackets with whitespace and a comma between them.
                            if (all_vals.length > 0) {
                                // the first group is positive values
                                for (String pval : all_vals[0].split("\\|\\|")) {
                                    propertySpec.addPositiveValue(pval);
                                }
                            }
                            if (all_vals.length == 2) {
                                // second group are negative values
                                for (String nval : all_vals[1].split("\\|\\|")) {
                                    propertySpec.addNegativeValue(nval);
                                }
                            }
                        } else { // no markup means positive values only
                            for (String pos_val : cellContent.split("\\|\\|")) {
                                propertySpec.addPositiveValue(pos_val);
                            }
                        }
                        varspec.addPropertySpec(propertySpec);
                    }
                }
                dataSpec.addVariableSpec(varspec);
            }
        }
    }

    /**
     * The second sheet of the xls file contains specifications for the groups.
     * See wiki documentation for more info.
     *
     * @param workbook
     */
    private void processXLSSheetTwo(Workbook workbook) {
        if (workbook.getNumberOfSheets() < 2) {
            return;
        }
        Sheet sheet2 = (Sheet) workbook.getSheetAt(1);
        Map<Integer, String> columnLabels = new HashMap<Integer, String>();
        for (Row row : sheet2) {
            // scan for the beginning of the group definitions
            // which are indicated by a row whose first cell is named "group"
            Cell firstCell = row.getCell(0);
            if (firstCell == null) {
                continue; // ignore rows without data in the first cell
            } else if (firstCell.getCellType() == Cell.CELL_TYPE_STRING
                    && firstCell.getStringCellValue().equalsIgnoreCase(GROUP_CELL)) {
                columnLabels.clear();
                for (Cell cell : row) {
                    // build a list of column labels
                    if (cell.getCellType() == Cell.CELL_TYPE_STRING) {
                        columnLabels.put(cell.getColumnIndex(), cell.getStringCellValue());
                    } else {
                        LOG.debug("Skipping xls column label cell (" + cell.getRowIndex() + "," + cell.getColumnIndex() + ") because it has non-string content");
                        continue;
                    }
                }
            } // if it's a row defining a group spec then this first cell will  be under the column label GROUP_CELL
            else {
                String firstColumnLabel = columnLabels.get(firstCell.getColumnIndex());
                if (firstColumnLabel == null) {
                    LOG.debug("Skipping xls row " + row.getRowNum() + " because its first element doesn't match any column label");
                    columnLabels.clear();
                    continue;
                }

                if (firstCell.getCellType() != Cell.CELL_TYPE_STRING
                        || !firstColumnLabel.equalsIgnoreCase(GROUP_CELL)) {
                    LOG.debug("Skipping xls row " + row.getRowNum() + " because its first element is not a string in column " + GROUP_CELL);
                    columnLabels.clear();
                    continue;
                }

                // build a group spec across the columns
                GroupSpec groupSpec = new GroupSpec(firstCell.getStringCellValue());
                for (Cell cell : row) {

                    String columnLabel = columnLabels.get(cell.getColumnIndex());
                    if (columnLabel == null) {
                        LOG.debug("Skipping xls cell (" + cell.getRowIndex() + "," + cell.getColumnIndex() + ") because it doesn't fall under any column label");
                        continue;
                    }

                    if (cell.getCellType() != Cell.CELL_TYPE_STRING) {
                        cell.setCellType(Cell.CELL_TYPE_STRING); // attempt to convert non string cells to strings
                    }
                    String cellContent = cell.getStringCellValue();

                    if (columnLabel.equalsIgnoreCase(INSTANCES_PER_PARENT_LABEL)) {
                        try {
                            int n = Integer.parseInt(cellContent);
                            groupSpec.setNumPerParent(n);
                        } catch (NumberFormatException e) {
                            LOG.error("Cannot parse: " + cellContent + " as an int. Check for typos in group specs.");
                            throw e;
                        }
                    } else if (columnLabel.equalsIgnoreCase(UNIQUE_ELEM_LABEL)) {
                        // break it out into a list<string>
                        for (String elem : cellContent.split("\\|\\|")) {
                            groupSpec.addUniqueElement(elem);
                        }
                    } else if (columnLabel.equalsIgnoreCase(PARENT_GROUP_LABEL)) {
                        groupSpec.setParentGroupName(cellContent);
                    }
                }
                dataSpec.addGroupSpec(groupSpec);
            }
        }
    }

}
