package page.basis;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.MouseEvent;
import java.util.List;

import javax.swing.JScrollPane;
import javax.swing.ListSelectionModel;
import javax.swing.event.ListSelectionEvent;
import javax.swing.event.ListSelectionListener;

import common.battle.BasisSet;
import common.util.unit.Form;
import page.JBTN;
import page.JTF;
import page.JTG;
import page.Page;
import page.info.filter.UnitFilterBox;
import page.info.filter.UnitListTable;

public class UnitFLUPage extends LubCont {

	private static final long serialVersionUID = 1L;

	private LineUpBox lub = new LineUpBox(this);
	private final JBTN back = new JBTN(0, "back");
	private final JTG show = new JTG(0, "showf");
	private final UnitListTable ult = new UnitListTable(this);
	private final JScrollPane jsp = new JScrollPane(ult);
	private final UnitFilterBox ufb;
	private final JTF seatf = new JTF();
	private final JBTN seabt = new JBTN();

	public UnitFLUPage(Page p) {
		super(p);

		ufb = UnitFilterBox.getNew(this, null);
		ini();
		resized();
	}

	@Override
	@SuppressWarnings("unchecked")
	public void callBack(Object o) {
		if (o instanceof List)
			ult.setList((List<Form>) o);
		resized();
	}

	public List<Form> getList() {
		return ult.list;
	}

	@Override
	protected LineUpBox getLub() {
		return lub;
	}

	@Override
	protected void mouseClicked(MouseEvent e) {
		if (e.getSource() == ult)
			ult.clicked(e.getPoint());
		super.mouseClicked(e);
	}

	@Override
	protected void renew() {
		lub.setLU(BasisSet.current.sele.lu);
	}

	@Override
	protected void resized(int x, int y) {
		setBounds(0, 0, x, y);
		set(back, x, y, 0, 0, 200, 50);
		set(show, x, y, 250, 0, 200, 50);
		set(seatf, x, y, 550, 0, 1000, 50);
		set(seabt, x, y, 1600, 0, 200, 50);
		int[] end = new int[] { 650, 350 };
		if (show.isSelected()) {
			int[] siz = ufb.getSizer();
			set(ufb, x, y, 50, 100, siz[0], siz[1]);
			int mx = 50, my = 100, ax = 2200, ay = 1150;
			if (siz[2] == 0) {
				mx += siz[3];
				ax -= siz[3];
				ay -= end[1 - siz[2]];
			} else {
				my += siz[3];
				ax -= end[1 - siz[2]];
				ay -= siz[3];
			}
			set(jsp, x, y, mx, my, ax, ay);
		} else
			set(jsp, x, y, 50, 100, 1550, 1150);
		set(lub, x, y, 1650, 950, 600, 300);
		ult.setRowHeight(size(x, y, 50));
	}

	private void addListeners() {
		back.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				changePanel(getFront());
			}
		});

		show.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent arg0) {
				if (show.isSelected())
					add(ufb);
				else
					remove(ufb);
			}
		});

		ListSelectionModel lsm = ult.getSelectionModel();
		lsm.setSelectionMode(ListSelectionModel.SINGLE_SELECTION);

		lsm.addListSelectionListener(new ListSelectionListener() {

			@Override
			public void valueChanged(ListSelectionEvent e) {
				if (lsm.getValueIsAdjusting())
					return;
				int ind = lsm.getAnchorSelectionIndex();
				if (ind < 0)
					return;
				Form f = ult.list.get(ind);
				lub.select(f);
			}

		});
		
		seabt.setLnr((b) -> {
			if(ufb != null) {
				ufb.name = seatf.getText();
				
				ufb.callBack(null);
			}
		});
		
		seatf.addActionListener(new ActionListener() {
			@Override
			public void actionPerformed(ActionEvent e) {
				if(ufb != null) {
					ufb.name = seatf.getText();
					
					ufb.callBack(null);
				}
			}
		});

	}

	private void ini() {
		add(back);
		add(show);
		add(ufb);
		add(jsp);
		add(lub);
		add(seatf);
		add(seabt);
		show.setSelected(true);
		addListeners();
	}

}
