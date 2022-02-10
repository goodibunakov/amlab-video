package ru.goodibunakov.amlabvideo.presentation.fragments

import android.content.DialogInterface
import android.os.Bundle
import android.view.*
import androidx.core.view.isVisible
import androidx.fragment.app.Fragment
import androidx.fragment.app.viewModels
import androidx.recyclerview.widget.DefaultItemAnimator
import androidx.recyclerview.widget.DividerItemDecoration
import androidx.recyclerview.widget.LinearLayoutManager
import by.kirich1409.viewbindingdelegate.viewBinding
import com.google.android.material.snackbar.Snackbar
import ru.goodibunakov.amlabvideo.AmlabApplication
import ru.goodibunakov.amlabvideo.R
import ru.goodibunakov.amlabvideo.databinding.FragmentMessagesBinding
import ru.goodibunakov.amlabvideo.presentation.recycler_utils.MessagesAdapter
import ru.goodibunakov.amlabvideo.presentation.viewmodels.MessagesViewModel


class MessagesFragment : Fragment(R.layout.fragment_messages) {

    private val viewModel: MessagesViewModel by viewModels { AmlabApplication.viewModelFactory }

    private val binding by viewBinding(FragmentMessagesBinding::bind)
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
        binding.messagesRecycler.apply {
            setHasFixedSize(true)
            adapter = messagesAdapter
            layoutManager = linearLayoutManager
            addItemDecoration(
                DividerItemDecoration(
                    binding.messagesRecycler.context,
                    DividerItemDecoration.VERTICAL
                )
            )
            itemAnimator = DefaultItemAnimator()
        }
    }

    private fun observeLiveData() {
        viewModel.messagesLiveData.observe(viewLifecycleOwner) {
            messagesAdapter.addItems(it)
            binding.messagesRecycler.smoothScrollToPosition(0)
        }

        viewModel.errorGetMessagesLiveData.observe(viewLifecycleOwner) {
            binding.errorText.isVisible = it != null
        }

        viewModel.progressbarLiveData.observe(viewLifecycleOwner) {
            binding.progressBar.isVisible = it
        }

        viewModel.errorDeleteMessagesLiveData.observe(viewLifecycleOwner) {
            it?.let {
                Snackbar.make(binding.root, R.string.error_delete_messages, Snackbar.LENGTH_SHORT)
                    .show()
            }
        }

        viewModel.emptyMessagesLiveData.observe(viewLifecycleOwner) {
            binding.emptyText.isVisible = it
        }
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