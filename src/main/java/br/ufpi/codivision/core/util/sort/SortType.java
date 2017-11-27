package br.ufpi.codivision.core.util.sort;

public enum SortType {  
  ASC(1), DESC(-1);  
    
  private int index = 1;  
    
     
  private SortType(int index) {  
     this.index = index;  
  }  
    
  public int getIndex() {  
     return this.index;  
  }  
}  
