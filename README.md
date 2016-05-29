# Team
teamwork


public class Math {


	public static void main(String[] args) {

		double a = -0.5 ;
		
//   输出a的绝对值      abs()返回绝对值

        System.out.println("a的绝对值："+StrictMath.abs(a));
        
//   acos()  返回一个值的反余弦

        System.out.println("a的反余弦值："+StrictMath.acos(a));
        
//   对两个数进行取余

        double b = 0.1 ;
        System.out.println(StrictMath.IEEEremainder(a, b));
        
//   输出一个接近或等于a的整数的浮点数  

        System.out.println("最接近a的整数："+StrictMath.rint(a));
        
//   输出a*2^c

        int c = 3 ;
        System.out.println("a*2^c="+StrictMath.scalb(a,c));
        
	}
