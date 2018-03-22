/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package AI_package;

/**
 *
 * @author Administrator
 */
public class NeuralNet {
    private double[] input;
    private double[][] weight1;
    private double[] weight2;
    private double[] hide;
    private double output;
    private double alpha;
    public NeuralNet(double alpha) {
        this.alpha=alpha;
        weight1=new double[8][50];
        hide=new double[50];
        weight2=new double[50];
    }
    public NeuralNet(double[][] weight1,double[] weight2,double alpha){
        this.input=input;
        this.alpha=alpha;
        this.weight1=weight1;
        this.weight2=weight2;
        this.hide=new double[weight2.length];
    }
    public void setInput(double[] input){
        double sum=0;
        this.input=input;
//      做归一化处理
        for(int i=0;i<input.length;i++)
            sum+=input[i];
        for(int i=0;i<input.length;i++)
            input[i]/=sum;
    }
    public void cal(){
        output=0;
        //隐含层归零
        for(int i=0;i<hide.length;i++)
            hide[i]=0;
        //计算隐含层和输出
        for(int i=0;i<weight1[0].length;i++){
            for(int j=0;j<weight1.length;j++){
                hide[i]+=input[j]*weight1[i][j];
            }
        }
        
        for(int i=0;i<hide.length;i++){
            
        }
            
    }
}
