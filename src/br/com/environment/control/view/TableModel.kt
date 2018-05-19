package br.com.environment.control.view

import br.com.environment.control.protocol.TableDataSource
import br.com.environment.control.protocol.TableDelegate
import javax.swing.SwingUtilities
import javax.swing.table.AbstractTableModel

class TableModel(dataSource: TableDataSource, delegate: TableDelegate): AbstractTableModel(),
        TableDataSource by dataSource, TableDelegate by delegate {

    override fun getRowCount() = numberOfRows()

    override fun getColumnCount() = numberOfColumns()

    override fun getValueAt(row: Int, column: Int) = valueAt(row, column)

    override fun getColumnName(index: Int) = columnNameAt(index)

    override fun isCellEditable(rowIndex: Int, columnIndex: Int) = false

    override fun setValueAt(aValue: Any?, rowIndex: Int, columnIndex: Int) {

        val change = aValue as? String ?: return

        if(columnIndex == 0) {
            didChangeName(rowIndex, change)
        } else {
            didChangePhoneNumber(rowIndex, change)
        }

    }

    fun reloadData() {
        SwingUtilities.invokeLater { fireTableDataChanged() }
    }

}