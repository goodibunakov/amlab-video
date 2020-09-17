package ru.goodibunakov.amlabvideo.presentation.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import com.google.android.material.snackbar.Snackbar
import kotlinx.android.synthetic.main.fragment_messages.*
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.presentation.recycler_utils.MessagesAdapter
import ru.goodibunakov.amlabvideo.presentation.utils.setVisibility
import ru.goodibunakov.amlabvideo.presentation.viewmodels.MessagesViewModel


class MessagesFragment : Fragment(R.layout.fragment_messages) {

    private val viewModel: MessagesViewModel by viewModels { AmlabApplication.viewModelFactory }
    private lateinit var messagesAdapter: MessagesAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setHasOptionsMenu(true)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)

        initRecyclerView()
        observeLiveData()
    }

    private fun initRecyclerView() {
        messagesAdapter = MessagesAdapter()
        val linearLayoutManager = LinearLayoutManager(requireContext())
        messagesRecycler.apply {
            setHasFixedSize(true)
            adapter = messagesAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(DividerItemDecoration(messagesRecycler.context, DividerItemDecoration.VERTICAL))
            itemAnimator = DefaultItemAnimator()
        }
    }

    private fun observeLiveData() {
        viewModel.messagesLiveData.observe(viewLifecycleOwner, {
            messagesAdapter.addItems(it)
            messagesRecycler.smoothScrollToPosition(0)
        })

        viewModel.errorGetMessagesLiveData.observe(viewLifecycleOwner, {
            errorText.setVisibility(it != null)
        })

        viewModel.progressbarLiveData.observe(viewLifecycleOwner, {
            progressBar.setVisibility(it)
        })

        viewModel.errorDeleteMessagesLiveData.observe(viewLifecycleOwner, {
            it?.let {
                Snackbar.make(root, R.string.error_delete_messages, Snackbar.LENGTH_SHORT).show()
            }
        })

        viewModel.emptyMessagesLiveData.observe(viewLifecycleOwner, {
            emptyText.setVisibility(it)
        })
    }

    override fun onCreateOptionsMenu(menu: Menu, inflater: MenuInflater) {
        inflater.inflate(R.menu.messages_menu, menu)
        super.onCreateOptionsMenu(menu, inflater)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        return when (item.itemId) {
            R.id.menuDeleteAllMessages -> {
                showWarningDialog()
                true
            }
            else -> super.onOptionsItemSelected(item)
        }
    }

    private fun showWarningDialog() {
        if (messagesAdapter.itemCount > 0) {
            WarningDialog.generate(context = requireContext(),
                    title = R.string.dialog_error_quota_title,
                    message = R.string.dialog_warning_delete_all_messages,
                    showCancelButton = true,
                    listener = { dialog, which ->
                        if (which == DialogInterface.BUTTON_POSITIVE) {
                            viewModel.deleteAllMessages()
                        } else {
                            dialog.cancel()
                        }
                    }).show()
        }
    }
}