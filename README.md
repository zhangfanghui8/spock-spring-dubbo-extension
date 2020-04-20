# spock-spring-dubbo-extension
 
In the project of spring Dubbo, the jar of spock-spring
can not mock  bean that the member variables of the Java class containing the annotation @reference.

this jar can solve this problem,you just need to load this jar.

for instance:

public interface FinanceOrderAppService{
String test();
}

@Component

Class ClassA{
    @Reference
    private DubboService dubboService
    
    public String testA(){
          return dubboService.test();
    }
}

Class Test extends Specification{
    @Autowired
    private ClassA classA
    
    @SpringBean
    DubboService dubboService = Mock() {
        test() >> "mock"
    }
    
     def "test"() {
     when:
     def a = classA.testA()
     then:
     a == "mock"
     }
}




In this example,if you add this jar,the DubboService bean can be mocked
