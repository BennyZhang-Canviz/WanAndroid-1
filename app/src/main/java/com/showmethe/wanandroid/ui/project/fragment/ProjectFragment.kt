package com.showmethe.wanandroid.ui.project.fragment


import android.graphics.Matrix
import android.graphics.RectF
import android.os.Bundle
import android.os.Parcelable
import android.view.View
import androidx.core.app.SharedElementCallback
import androidx.databinding.ObservableArrayList
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView


import com.showmethe.wanandroid.dialog.DropWindow

import com.showmethe.wanandroid.R
import com.showmethe.wanandroid.databinding.FragmentProjectBinding
import com.showmethe.wanandroid.entity.CateBean
import com.showmethe.wanandroid.ui.home.openDetail
import com.showmethe.wanandroid.ui.main.startToImg
import com.showmethe.wanandroid.ui.main.vm.MainViewModel
import com.showmethe.wanandroid.ui.project.adapter.ProjectAdapter
import com.showmethe.wanandroid.util.HoldViewHelper
import kotlinx.android.synthetic.main.fragment_accunt.*
import kotlinx.android.synthetic.main.fragment_project.*
import kotlinx.android.synthetic.main.fragment_project.toolbar
import showmethe.github.core.base.LazyFragment
import showmethe.github.core.base.vmpath.VMRouter
import showmethe.github.core.util.widget.StatusBarUtil
import showmethe.github.core.util.widget.StatusBarUtil.fixToolbar


class ProjectFragment : LazyFragment<FragmentProjectBinding, MainViewModel>() {

    
    lateinit var window: DropWindow
    private val pagerNumber = MutableLiveData<Int>()
    private var currentId = -1
    private lateinit var adapter: ProjectAdapter
    private val  list = ObservableArrayList<CateBean.DatasBean>()

    override fun initViewModel(): MainViewModel = createViewModel()
    override fun getViewId(): Int = R.layout.fragment_project

    override fun onBundle(bundle: Bundle) {
    }




    override fun observerUI() {

        viewModel.cateTab.observe(this, Observer {
            it?.apply {
                response?.apply {
                    if(size>0){
                        tvProject.text = this[0].name
                        currentId = this[0].id
                        pagerNumber.value = 0
                    }
                    window.addList(this)
                }
            }
        })

        pagerNumber.observe(this, Observer {
            it?.apply {
                if(currentId!=-1){
                    router.toTarget("getCate",this,currentId)
                }else{
                    refresh.isRefreshing = false
                }
            }
        })

        viewModel.cate.observe(this, Observer {
            it?.apply {
                refresh.isRefreshing = false
                if(pagerNumber.value!! == 0){
                    list.clear()
                }
                response?.apply {
                    list.addAll(this.datas)
                    onLoadSize(this.datas.size)
                }

            }
        })


    }

    override fun init() {
        fixToolbar(toolbar)
        refresh.setColorSchemeResources(R.color.color_f4511e)

        window = DropWindow(context)

        adapter = ProjectAdapter(context,list)
        rv.adapter  = adapter
        rv.layoutManager = LinearLayoutManager(context,RecyclerView.HORIZONTAL,false)

        
        router.toTarget("getCateTab")
    }


    override fun initListener() {


        tvProject.setOnClickListener {
            window.showAtBottom(it)
        }

        window.setOnItemClickListener { id, name ->
            currentId = id
            tvProject.text = name
            pagerNumber.value = 0
            window.dismiss()
        }

        refresh.setOnRefreshListener {
            pagerNumber.value = 0
        }


        profile.setOnClickListener {
            viewModel.openDrawer.value = true
        }


        adapter.setOnLikeClickListener { item, isCollect,pos ->
            if(isCollect){
                viewModel.homeCollect(item.id)
            }else{
                viewModel.homeUnCollect(item.id)
            }
        }

        adapter.setOnImgClickListener { view, item ->
            context.startToImg(item,view)
            HoldViewHelper.get().holdView(view)
        }
        adapter.setOnItemClickListener { view, position ->
            context.openDetail(list[position].link)
        }

        context.setExitSharedElementCallback(object : SharedElementCallback(){

            override fun onCaptureSharedElementSnapshot(
                sharedElement: View?,
                viewToGlobalMatrix: Matrix?,
                screenBounds: RectF?
            ): Parcelable {
                sharedElement?.alpha = 1f
                return super.onCaptureSharedElementSnapshot(
                    sharedElement,
                    viewToGlobalMatrix,
                    screenBounds
                )
            }
        })

    }


    override fun onDestroyView() {
        super.onDestroyView()
    }


    private fun onLoadSize(size: Int) {
        refresh.isRefreshing = false
        rv.finishLoading()
        if(size == 0){
            rv.setEnableLoadMore(false)
        }else{
            rv.setEnableLoadMore(true)
        }
    }

}