import java.util.*;
import java.time.LocalTime;
import java.time.Duration;
import java.time.format.DateTimeFormatter;

class Details{
    String parkingSlot;
    String carNo;
    String chackINTime;
    String chackOutTime;
    int charges;
    String category;
}

class Floorslots {
    int totalFloors;
    int totalSlots;
    String resevedscSlots;
    List<String> reservedscSlotsarr = new ArrayList<String>();
    List<String> totalSlotsarr = new ArrayList<String>();
    List<String> bookedreservedSlot = new ArrayList<String>();
    List<String> bookedunreservedSlot = new ArrayList<String>();

    Floorslots(int i, int j, String resevedSlots) {
        this.totalFloors = i;
        this.totalSlots = j;
        this.resevedscSlots = resevedSlots;
        arraySlots(resevedscSlots);
        totalSlotsmatrix(i, j);
    }

    public void arraySlots(String slots) {

        String[] splitedSlots = slots.split(" ");
        this.reservedscSlotsarr.addAll(Arrays.asList(splitedSlots));
    }

    public void totalSlotsmatrix(int i, int j) {
        for (int x = 1; x <= i; x++) {
            for (int k = 1; k <= j; k++) {
                String slot = (char) (x + 64) + "-" + (k);
                this.totalSlotsarr.add(slot);
            }
        }
        this.totalSlotsarr.removeAll(this.reservedscSlotsarr);
    }
}

public class Car_paarking {
    static Floorslots f1 = new Floorslots(5, 10, "A-1 A-10 B-2 B-5 C-1 C-8 D-2 D-4 E-5 E-10");
    static List<Details> data = new ArrayList<>();
    static List<Details> reportData = new ArrayList<>();

    public static void main(String[] args) {

        Scanner inp = new Scanner(System.in);

        boolean inputLoop = true;

        while (inputLoop) {
            String inputString = inp.nextLine();

            String[] inpStr = inputString.split(" ");

            if (inpStr[0].matches("CHECKIN") && inpStr.length == 4) {
                String allotedSlot = chackin(inpStr[1], inpStr[2], inpStr[3]);
                
                System.out.println(allotedSlot);
            }
            else if (inpStr[0].matches("CHECKOUT") && inpStr.length == 3) {
                int charges = chackout(inpStr[1], inpStr[2]);
                if(charges == -1){
                    System.out.println("Either you entered wrong CAR NUMBER or The car is already chackedout");
                }
                else{
                    System.out.println(charges);
                }
                
            }
            else if (inputString.matches ("GENERATE REPORT") && inpStr.length == 2) {
                System.out.println("PARKING SLOT, CAR NO, CHACK IN TIME, CHACK OUT TIME, CHARGES, CATEGORY");
                generateResponse();
            }
            else{
                inputLoop = false;
            }
        }
        inp.close();
    }

    public static String chackin(String cicarNo, String citime, String cicatagory ){
        String slot;
            for (Details cd : data) {
                if(cd.carNo.equals(cicarNo)){
                    return "the car is already in parking";
                }
            }
            if(cicatagory.matches("R")){
                Details newData = new Details();
                if(!(f1.reservedscSlotsarr.isEmpty())){
                    slot = f1.reservedscSlotsarr.remove(0);
                    f1.bookedreservedSlot.add(slot); 
                }
                else{
                    slot = f1.totalSlotsarr.remove(0);
                    f1.bookedunreservedSlot.add(slot);
                }
                newData.carNo = cicarNo;
                newData.chackINTime = citime;
                newData.category = cicatagory;
                newData.parkingSlot = slot;
                data.add(newData);     
            }
            else if (!(f1.totalSlotsarr.isEmpty())){
                Details newData = new Details();

                slot = f1.totalSlotsarr.remove(0);
                f1.bookedunreservedSlot.add(slot);

                newData.carNo = cicarNo;
                newData.chackINTime = citime;
                newData.category = cicatagory;
                newData.parkingSlot = slot;
                data.add(newData);
            }
            else{
                slot = "PARKING SLOTS ARE FULL...";
            }
            
        return slot;
    }

    public static int chackout(String cocarno, String cotime){
        int duration;
        int charge;
        for (Details d : data) {
            if(d.carNo.matches(cocarno)){
                duration = durationofcar(d.chackINTime, cotime);
                if(duration <= 120){
                    charge = 50;
                }
                else if (duration > 120 && duration <= 240){
                    charge = 80;
                }
                else{
                    charge = 100;
                }
                d.chackOutTime=cotime;
                d.charges=charge;
                if(f1.bookedreservedSlot.contains(d.parkingSlot)){
                    int index = Collections.binarySearch(f1.reservedscSlotsarr, d.parkingSlot);
                    if (index < 0) {
                        index = -(index + 1);
                    }
                    f1.reservedscSlotsarr.add(index, d.parkingSlot);

                    f1.bookedreservedSlot.remove(d.parkingSlot);
                }
                else if(f1.bookedunreservedSlot.contains(d.parkingSlot)){
                    int index = Collections.binarySearch(f1.totalSlotsarr, d.parkingSlot);
                    if (index < 0) {
                        index = -(index + 1);
                    }
                    f1.totalSlotsarr.add(index, d.parkingSlot);
                    f1.bookedunreservedSlot.remove(d.parkingSlot);
                }
                reportData.add(d);
                data.remove(d);
            return charge;
            }
        }
        return -1;
    }

    static void generateResponse(){
        reportData.sort(Comparator.comparing((Details d) -> d.parkingSlot).thenComparing((Details d) -> d.chackINTime));
        for (Details d : reportData) {
            System.out.println(d.parkingSlot+", "+d.carNo+", "+d.chackINTime+", "+d.chackOutTime+", "+d.charges+", "+d.category);
        }
    }

    static int durationofcar(String st, String et) {
        String startTime = st;
        String endTime = et;

        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("h:ma");
        LocalTime start = LocalTime.parse(startTime, formatter);
        LocalTime end = LocalTime.parse(endTime, formatter);

        Duration duration = Duration.between(start, end);
        int minutes = (int)duration.toMinutes();

        return minutes;
    }
}
