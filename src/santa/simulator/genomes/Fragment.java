package santa.simulator.genomes;

/**
 * @author Andrew Rambaut
 * @version $Id$
 */
public final class Fragment {
	public Fragment(int start, int count) {
		assert(count >= 0);
		this.start = start;
		this.count = count;
	}

	// copy constructor
	public Fragment(Fragment f) {
		this.start = f.start;
		this.count = f.count;

	}

	/* (non-Javadoc)
	 * @see java.lang.Object#hashCode()
	 */
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + count;
		result = prime * result + start;
		return result;
	}


	/* (non-Javadoc)
	 * @see java.lang.Object#equals(java.lang.Object)
	 */
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (!(obj instanceof Fragment))
			return false;
		Fragment other = (Fragment) obj;
		if (count != other.count)
			return false;
		if (start != other.start)
			return false;
		return true;
	}


	public boolean adjacent(Fragment that) {
		return ((this.start + this.count + 1) == that.start || (that.start+that.count+1) == this.start);
	}

	public boolean overlaps(Fragment that) {
		if (this.start < that.start) 
			return ((this.start + this.count) >= that.start);
		else
			return ((that.start + that.count) >= this.start);
	}
		
	public void merge(Fragment that) {
		int last = Math.max(this.start+this.count, that.start+that.count);
		this.start = Math.min(this.start, that.start);
		this.count = last - this.start;
	}


		
	/** 
	 * copy constructor.   Clone a fragment and apply an indel.
	 * It is possible the indel will have no effect on the fragment in which
	 * case the cloned fragment will have identical values as the original.
	 * It is also possible that the indel will completely wipe out
	 * the fragment (reducing it's length to zero).
	 *
	 * Note: A negative delta indicates deletion from the current position moving right.  It DOES NOT mean to remove bases to the left of current position.
	 * 
	 * @param: f: fragment to be cloned.
	 * @param position: non-negative position where indel would begin
	 * @param delta: positive/negative count of positions to be inserted/deleted.
	 */
	public Fragment(Fragment f, int position, int delta) {
		assert(f.count > 0);
		if (position < f.start) {
			// delete: delta < 0
			// insert: delta > 0
			if (delta < 0) {
				// delete: delta < 0
				int shift = Math.min(-delta, f.start-position);
				int shrink = Math.min(-delta - shift, f.count);
				this.start = f.start - shift;
				this.count = f.count - shrink;
			} else {	
				this.start = f.start + delta;
				this.count = f.count;
			}
		}
		else if (position <= f.getFinish()) {
			this.start = f.start;
			if (delta < 0) {
				// delete: delta < 0
				delta = -Math.min(-delta, f.start+f.count-position);
			}
			this.count = f.count + delta;
		} else {
			this.start = f.start;
			this.count = f.count;
		}
		assert(this.start >= 0);
		assert(this.count >= 0);

	}

	public int getStart() {
		return start;
	}

	public int getFinish() {
		if (count <= 0) 
			throw new RuntimeException("getFinish() is meaningless on zero-length fragments");

		return start + count - 1;
	}

	public int getLength() {
		return count;
	}

	public void shift(int howmuch) {
		this.start += howmuch;
	}
		
	private int start;
	private int count;
}
