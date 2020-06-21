package models;

public class dTmodel implements java.io.Serializable {

	 private static final long serialVersionUID = 1L;

	 private Integer uid;
	 private Integer start;
	 private Integer end;
	 
	 public dTmodel() {
		 this.start = 0;
		 this.end = 4;
	 }

	 public Integer getUid() {
		 return this.uid;
	 }
	 
	 public void setUid(Integer uid) {
		 this.uid = uid;
	 }
	 
	 public Integer getStart() {
		 return this.start;
	 }
	 
	 public void setStart(Integer start) {
		 this.start = start;
	 }
	 
	 public Integer getEnd() {
		 return this.end;
	 }
	 
	 public void setEnd(Integer end) {
		 this.end = end;
	 }

}