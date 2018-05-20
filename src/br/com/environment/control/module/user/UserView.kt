package br.com.environment.control.module.user

import br.com.environment.control.extension.coolAppend
import br.com.environment.control.view.TableModel
import io.reactivex.disposables.CompositeDisposable
import java.awt.BorderLayout
import java.awt.Container
import java.awt.Dimension
import java.awt.GridLayout
import javax.swing.*

class UserView : JFrame("User") {

    private val disposables = CompositeDisposable()

    private val container: Container by lazy {
        contentPane
    }

    private var logArea: JTextArea
    private var table: JTable
    private val tableModel: TableModel
    private val enterBtn: JButton
    private val leaveBtn: JButton

    private val viewModel = UserViewModel()

    init {

        size = Dimension(600, 400)
        defaultCloseOperation = JFrame.EXIT_ON_CLOSE

        /**
         * Buttons
         * */
        enterBtn = JButton("Enter Environment")
        leaveBtn = JButton("Leave Environment")

        enterBtn.addActionListener { didTouchEnterBtn() }
        leaveBtn.addActionListener { didTouchLeaveBtn() }


        val bottomPanel = JPanel()
        bottomPanel.layout = GridLayout(1, 2)
        bottomPanel.add(enterBtn)
        bottomPanel.add(leaveBtn)

        container.add(bottomPanel, BorderLayout.SOUTH)


        /**
         * Table
         * */
        tableModel = TableModel(viewModel, viewModel)
        table = JTable(tableModel)
        val scroll = JScrollPane(table)

        table.fillsViewportHeight = true

        // Columns Widths
        val namesWidth = (0.6 * WIDTH).toInt()
        table.columnModel.getColumn(0).preferredWidth = namesWidth
        table.columnModel.getColumn(1).preferredWidth = WIDTH - namesWidth

        // Allow single selection
        table.setSelectionMode(ListSelectionModel.SINGLE_SELECTION)

        // Can select only an entire row
        table.rowSelectionAllowed = true
        table.columnSelectionAllowed = false

        container.add(scroll)


        /**
         * Log
         * */

        logArea = JTextArea()
        val scrollPane = JScrollPane(logArea)
        scrollPane.preferredSize = Dimension(250, 400)
        logArea.isEditable = false
        container.add(scrollPane, BorderLayout.EAST)


        // Finish
        isVisible = true
        container.repaint()
        requestFocus()

        subscribe()
        viewModel.setup()

    }

    private fun subscribe() {
        disposables.add(
                viewModel.error
                        .subscribe {
                            presentError(it)
                        }
        )

        disposables.add(
                viewModel.messages
                        .subscribe {
                            logArea.coolAppend(it)
                        }
        )

        disposables.add(
                viewModel.reload
                        .subscribe {
                            tableModel.reloadData()
                        }
        )

        disposables.add(
                viewModel.title
                        .subscribe {
                            title = it
                        }
        )

        disposables.add(
                viewModel.status
                        .subscribe {
                            updateView(it)
                        }
        )

    }

    private fun didTouchEnterBtn() {
        viewModel.enterEnvironment(table.selectedRow)
    }

    private fun didTouchLeaveBtn() {
        viewModel.leaveEnvironment()
    }

    private fun presentError(message: String) {
        JOptionPane.showMessageDialog(null, message, "Error", JOptionPane.ERROR_MESSAGE)
    }

    private fun updateView(status: UserViewModel.UserStatus) {
        enterBtn.isEnabled = status.isOutside
        leaveBtn.isEnabled = status.isInside
    }


}

fun main(args: Array<String>) {
    UserView()
}