package page.info.edit

import common.pack.PackData.UserPack
import common.pack.UserProfile
import common.util.stage.MapColc
import common.util.stage.Stage
import common.util.stage.StageMap
import common.util.unit.Enemy
import main.Opts
import page.JBTN
import page.Page
import page.battle.BattleSetupPage
import page.battle.StRecdPage
import page.info.edit.LimitTable
import page.info.edit.SCGroupEditTable
import page.info.edit.StageEditTable
import page.info.filter.EnemyFindPage
import page.support.AnimLCR
import page.support.ReorderList
import page.support.ReorderListener
import java.awt.Rectangle
import java.awt.event.ActionEvent
import java.awt.event.ActionListener
import java.awt.event.MouseEvent
import java.util.*
import java.util.function.Consumer
import javax.swing.JList
import javax.swing.JScrollPane
import javax.swing.event.ListSelectionEvent
import javax.swing.event.ListSelectionListener

class StageEditPage(p: Page?, map: MapColc, pac: UserPack) : Page(p) {
    private val back: JBTN = JBTN(0, "back")
    private val strt: JBTN = JBTN(0, "start")
    private val veif: JBTN = JBTN(0, "veif")
    private val cpsm: JBTN = JBTN(0, "cpsm")
    private val cpst: JBTN = JBTN(0, "cpst")
    private val ptsm: JBTN = JBTN(0, "ptsm")
    private val ptst: JBTN = JBTN(0, "ptst")
    private val rmsm: JBTN = JBTN(0, "rmsm")
    private val rmst: JBTN = JBTN(0, "rmst")
    private val recd: JBTN = JBTN(0, "replay")
    private val elim: JBTN = JBTN(0, "limit")
    private val jt: StageEditTable
    private val jspjt: JScrollPane
    private val jlsm: ReorderList<StageMap> = ReorderList<StageMap>()
    private val jspsm: JScrollPane = JScrollPane(jlsm)
    private val jlst: ReorderList<Stage> = ReorderList<Stage>()
    private val jspst: JScrollPane = JScrollPane(jlst)
    private val lpsm: JList<StageMap> = JList<StageMap>(Stage.Companion.CLIPMC.maps)
    private val jlpsm: JScrollPane = JScrollPane(lpsm)
    private val lpst: JList<Stage> = JList<Stage>()
    private val jlpst: JScrollPane = JScrollPane(lpst)
    private val adds: JBTN = JBTN(0, "add")
    private val rems: JBTN = JBTN(0, "rem")
    private val addl: JBTN = JBTN(0, "addl")
    private val reml: JBTN = JBTN(0, "reml")
    private val advs: JBTN = JBTN(0, "advance")
    private val jle: JList<Enemy> = JList<Enemy>()
    private val jspe: JScrollPane = JScrollPane(jle)
    private val info: HeadEditTable
    private val mc: MapColc
    private val pack: String
    private val efp: EnemyFindPage?
    private var changing = false
    private var stage: Stage? = null
    override fun mouseClicked(e: MouseEvent) {
        if (e.source === jt && !e.isControlDown) jt.clicked(e.point)
    }

    override fun renew() {
        info.renew()
        if (efp != null && efp.getList() != null) jle.setListData(efp.getList().toTypedArray())
    }

    @Synchronized
    override fun resized(x: Int, y: Int) {
        setBounds(0, 0, x, y)
        Page.Companion.set(back, x, y, 0, 0, 200, 50)
        Page.Companion.set(info, x, y, 900, 50, 1400, 300)
        Page.Companion.set(addl, x, y, 900, 400, 200, 50)
        Page.Companion.set(reml, x, y, 1100, 400, 200, 50)
        Page.Companion.set(elim, x, y, 1600, 400, 200, 50)
        Page.Companion.set(recd, x, y, 1850, 400, 200, 50)
        Page.Companion.set(advs, x, y, 2100, 400, 200, 50)
        Page.Companion.set(jspjt, x, y, 900, 450, 1400, 900)
        Page.Companion.set(jspsm, x, y, 0, 50, 300, 800)
        Page.Companion.set(cpsm, x, y, 0, 850, 300, 50)
        Page.Companion.set(ptsm, x, y, 0, 900, 300, 50)
        Page.Companion.set(rmsm, x, y, 0, 950, 300, 50)
        Page.Companion.set(jlpsm, x, y, 0, 1000, 300, 300)
        Page.Companion.set(strt, x, y, 300, 0, 300, 50)
        Page.Companion.set(adds, x, y, 300, 50, 150, 50)
        Page.Companion.set(rems, x, y, 450, 50, 150, 50)
        Page.Companion.set(jspst, x, y, 300, 100, 300, 750)
        Page.Companion.set(cpst, x, y, 300, 850, 300, 50)
        Page.Companion.set(ptst, x, y, 300, 900, 300, 50)
        Page.Companion.set(rmst, x, y, 300, 950, 300, 50)
        Page.Companion.set(jlpst, x, y, 300, 1000, 300, 300)
        Page.Companion.set(veif, x, y, 600, 0, 300, 50)
        Page.Companion.set(jspe, x, y, 600, 50, 300, 1250)
        jt.rowHeight = Page.Companion.size(x, y, 50)
    }

    private fun `addListeners$0`() {
        back.setLnr(Consumer { x: ActionEvent? -> changePanel(front) })
        strt.setLnr(Consumer { x: ActionEvent? -> changePanel(BattleSetupPage(getThis(), stage)) })
        advs.setLnr(Consumer { x: ActionEvent? -> changePanel(AdvStEditPage(getThis(), stage)) })
        recd.setLnr(Consumer { x: ActionEvent? -> changePanel(StRecdPage(getThis(), stage, true)) })
        elim.setLnr(Consumer { x: ActionEvent? -> changePanel(LimitEditPage(getThis(), stage)) })
        addl.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val ind: Int = jt.addLine(jle.selectedValue)
                setData(stage)
                if (ind < 0) jt.clearSelection() else jt.addRowSelectionInterval(ind, ind)
            }
        })
        reml.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val ind: Int = jt.remLine()
                setData(stage)
                if (ind < 0) jt.clearSelection() else jt.addRowSelectionInterval(ind, ind)
            }
        })
        veif.setLnr(Consumer { x: ActionEvent? -> changePanel(efp) })
    }

    private fun `addListeners$1`() {
        jlsm.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent) {
                if (changing || arg0.valueIsAdjusting) return
                changing = true
                setAA(jlsm.selectedValue)
                changing = false
            }
        })
        jlst.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent) {
                if (changing || arg0.valueIsAdjusting) return
                changing = true
                setAB(jlst.selectedValue)
                changing = false
            }
        })
        jlsm.list = object : ReorderListener<StageMap?> {
            override fun reordered(ori: Int, fin: Int) {
                val lsm: MutableList<StageMap> = ArrayList<StageMap>()
                for (sm in mc.maps) lsm.add(sm)
                val sm: StageMap = lsm.removeAt(ori)
                lsm.add(fin, sm)
                mc.maps = lsm.toTypedArray()
                changing = false
            }

            override fun reordering() {
                changing = true
            }
        }
        jlst.list = object : ReorderListener<Stage?> {
            override fun reordered(ori: Int, fin: Int) {
                changing = false
                val l = stage!!.map.list
                val sta = l.removeAt(ori)
                l.add(fin, sta)
            }

            override fun reordering() {
                changing = true
            }
        }
        lpsm.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent) {
                if (changing || arg0.valueIsAdjusting) return
                changing = true
                setBA(lpsm.selectedValue)
                changing = false
            }
        })
        lpst.addListSelectionListener(object : ListSelectionListener {
            override fun valueChanged(arg0: ListSelectionEvent) {
                if (changing || arg0.valueIsAdjusting) return
                changing = true
                setBB(lpst.selectedValue)
                changing = false
            }
        })
    }

    private fun `addListeners$2`() {
        cpsm.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val sm: StageMap = jlsm.selectedValue
                val col: MapColc = Stage.Companion.CLIPMC
                val copy: StageMap = sm.copy(col)
                val n: Int = col.maps.size
                col.maps = Arrays.copyOf(col.maps, n + 1)
                col.maps.get(n) = copy
                changing = true
                lpsm.setListData(col.maps)
                lpsm.setSelectedValue(copy, true)
                setBA(copy)
                changing = false
            }
        })
        cpst.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val copy = stage!!.copy(Stage.Companion.CLIPSM)
                Stage.Companion.CLIPSM.add(copy)
                changing = true
                lpst.setListData(Vector<Stage>(Stage.Companion.CLIPSM.list))
                lpst.setSelectedValue(copy, true)
                lpsm.selectedIndex = 0
                setBB(copy)
                changing = false
            }
        })
        ptsm.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val sm: StageMap = lpsm.selectedValue
                val ni: StageMap = sm.copy(mc)
                val n: Int = mc.maps.size
                mc.maps = Arrays.copyOf(mc.maps, n + 1)
                mc.maps.get(n) = ni
                changing = true
                jlsm.setListData(mc.maps)
                jlsm.setSelectedValue(ni, true)
                setBA(ni)
                changing = false
            }
        })
        ptst.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val sm: StageMap = jlsm.selectedValue
                stage = lpst.selectedValue.copy(sm)
                sm.add(stage)
                changing = true
                jlst.setListData(sm.list.toTypedArray())
                jlst.setSelectedValue(stage, true)
                setBB(stage)
                changing = false
            }
        })
        rmsm.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val ind: Int = lpsm.selectedIndex
                val col: MapColc = Stage.Companion.CLIPMC
                val sms: Array<StageMap?> = arrayOfNulls<StageMap>(col.maps.size - 1)
                for (i in 0 until ind) sms[i] = col.maps.get(i)
                for (i in ind until col.maps.size - 1) sms[i] = col.maps.get(i + 1)
                col.maps = sms
                changing = true
                lpsm.setListData(sms)
                lpsm.selectedIndex = ind - 1
                setBA(lpsm.selectedValue)
                changing = false
            }
        })
        rmst.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val sm: StageMap = lpsm.selectedValue
                val st: Stage = lpst.selectedValue
                val ind: Int = lpst.selectedIndex
                sm.list.remove(st)
                changing = true
                lpst.setListData(Vector<Stage>(sm.list))
                lpst.selectedIndex = ind - 1
                setBB(lpst.selectedValue)
                changing = false
            }
        })
        adds.addActionListener(object : ActionListener {
            override fun actionPerformed(arg0: ActionEvent?) {
                val sm: StageMap = jlsm.selectedValue
                stage = Stage(sm)
                sm.add(stage)
                changing = true
                jlst.setListData(sm.list.toTypedArray())
                jlst.setSelectedValue(stage, true)
                setAB(stage)
                changing = false
            }
        })
        rems.setLnr(Consumer { x: ActionEvent? ->
            if (!Opts.conf()) return@setLnr
            val sm: StageMap = jlsm.selectedValue
            var ind: Int = jlst.selectedIndex - 1
            sm.list.remove(stage)
            changing = true
            jlst.setListData(Vector<Stage>(sm.list))
            if (ind < 0) ind = -1
            if (ind < sm.list.size) jlst.setSelectedIndex(ind) else jlst.setSelectedIndex(sm.list.size - 1)
            setAB(jlst.selectedValue)
            changing = false
        })
    }

    private fun checkPtsm() {
        val sm: StageMap = lpsm.selectedValue
        if (sm == null) {
            ptsm.isEnabled = false
            return
        }
        var b = true
        for (st in sm.list) b = b and st.isSuitable(pack)
        ptsm.isEnabled = b
    }

    private fun checkPtst() {
        val st: Stage = lpst.selectedValue
        val sm: StageMap = jlsm.selectedValue
        if (st == null || sm == null) ptst.isEnabled = false else ptst.isEnabled = st.isSuitable(pack)
        rmst.isEnabled = st != null
    }

    private fun ini() {
        add(back)
        add(veif)
        add(adds)
        add(rems)
        add(jspjt)
        add(info)
        add(strt)
        add(jspsm)
        add(jspst)
        add(addl)
        add(reml)
        add(jspe)
        add(cpsm)
        add(cpst)
        add(ptsm)
        add(ptst)
        add(rmsm)
        add(rmst)
        add(jlpsm)
        add(jlpst)
        add(recd)
        add(advs)
        add(elim)
        setAA(null)
        setBA(null)
        jle.cellRenderer = AnimLCR()
        `addListeners$0`()
        `addListeners$1`()
        `addListeners$2`()
    }

    private fun setAA(sm: StageMap?) {
        if (sm == null) {
            jlst.setListData(arrayOfNulls<Stage>(0))
            setAB(null)
            cpsm.isEnabled = false
            ptst.isEnabled = false
            adds.isEnabled = false
            return
        }
        jlst.setListData(Vector<Stage>(sm.list))
        if (sm.list.size == 0) {
            jlst.clearSelection()
            cpsm.isEnabled = false
            adds.isEnabled = true
            checkPtst()
            setAB(null)
            return
        }
        jlst.selectedIndex = 0
        cpsm.isEnabled = true
        adds.isEnabled = true
        checkPtst()
        setAB(sm.list.get(0))
    }

    private fun setAB(st: Stage?) {
        if (st == null) {
            setData(lpst.selectedValue)
            cpst.isEnabled = false
            rems.isEnabled = false
            return
        }
        cpst.isEnabled = true
        rems.isEnabled = true
        lpst.clearSelection()
        checkPtst()
        setData(st)
    }

    private fun setBA(sm: StageMap?) {
        if (sm == null) {
            lpst.setListData(arrayOfNulls<Stage>(0))
            ptsm.isEnabled = false
            rmsm.isEnabled = false
            setBB(null)
            return
        }
        lpst.setListData(Vector<Stage>(sm.list))
        rmsm.isEnabled = sm !== Stage.Companion.CLIPSM
        if (sm.list.size == 0) {
            lpst.clearSelection()
            ptsm.isEnabled = false
            setBB(null)
            return
        }
        lpst.selectedIndex = 0
        setBB(sm.list.get(0))
        checkPtsm()
    }

    private fun setBB(st: Stage?) {
        if (st == null) {
            setData(jlst.selectedValue)
            ptst.isEnabled = false
            rmst.isEnabled = false
            return
        }
        cpst.isEnabled = false
        checkPtst()
        jlst.clearSelection()
        setData(st)
    }

    private fun setData(st: Stage?) {
        stage = st
        info.setData(st)
        jt.setData(st)
        strt.isEnabled = st != null
        recd.isEnabled = st != null
        advs.isEnabled = st != null
        elim.isEnabled = st != null
        jspjt.scrollRectToVisible(Rectangle(0, 0, 1, 1))
        resized()
    }

    companion object {
        private const val serialVersionUID = 1L
        fun redefine() {
            StageEditTable.Companion.redefine()
            LimitTable.Companion.redefine()
            SCGroupEditTable.Companion.redefine()
        }
    }

    init {
        mc = map
        pack = pac.desc.id
        jt = StageEditTable(this, pac)
        jspjt = JScrollPane(jt)
        info = HeadEditTable(this, pac)
        jlsm.setListData(mc.maps)
        jle.setListData(UserProfile.Companion.getAll<Enemy>(pack, Enemy::class.java).toTypedArray())
        efp = EnemyFindPage(getThis(), pack)
        ini()
    }
}