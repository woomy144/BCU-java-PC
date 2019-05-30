package jogl.recd;

import java.awt.image.BufferedImage;
import java.util.ArrayDeque;
import java.util.Queue;

import jogl.awt.GLCstd;
import page.RetFunc;
import page.awt.RecdThread;

public class GLRecdBImg extends GLRecorder {

	private final Queue<BufferedImage> qb;
	private final RecdThread th;

	public GLRecdBImg(GLCstd scr, Queue<BufferedImage> lb, RecdThread rt) {
		super(scr);
		qb = lb;
		th = rt;
	}

	protected GLRecdBImg(GLCstd scr, String path, int type, RetFunc ob) {
		super(scr);
		qb = new ArrayDeque<BufferedImage>();
		th = RecdThread.getIns(ob, qb, path, type);
	}

	@Override
	public void end() {
		synchronized (th) {
			th.end = true;
		}
	}

	@Override
	public void quit() {
		synchronized (th) {
			th.quit = true;
		}
	}

	@Override
	public int remain() {
		int size;
		synchronized (qb) {
			size = qb.size();
		}
		return size;
	}

	@Override
	public void start() {
		th.start();
	}

	@Override
	public void update() {
		synchronized (qb) {
			qb.add(screen.getScreen());
		}
	}

}