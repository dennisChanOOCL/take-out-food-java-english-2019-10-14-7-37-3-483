import java.util.ArrayList;
import java.util.List;

/*
 * This Java source file was generated by the Gradle 'init' task.
 */


public class App {
    public static String fmt(double d)
    {
        if(d == (long) d)
            return String.format("%d",(long)d);
        else
            return String.format("%s",d);
    }

    private ItemRepository itemRepository;
    private SalesPromotionRepository salesPromotionRepository;

    public App(ItemRepository itemRepository, SalesPromotionRepository salesPromotionRepository) {
        this.itemRepository = itemRepository;
        this.salesPromotionRepository = salesPromotionRepository;
    }

    public String bestCharge(List<String> inputs) {
        //TODO: write code here
        String result = "============= Order details =============\n";
        List<Item> itemList = itemRepository.findAll();
        List<Item> orderedHalfPriceItemList = new ArrayList<>();
        List<SalesPromotion> saleList = salesPromotionRepository.findAll();
        SalesPromotion halfSalesPromotion =  saleList.stream().filter(
                e->e.getType()=="50%_DISCOUNT_ON_SPECIFIED_ITEMS")
                .findFirst()
                .orElse(null);

        SalesPromotion thirty_sub_six_Promotion =  saleList.stream().filter(
                e->e.getType()=="BUY_30_SAVE_6_YUAN")
                .findFirst()
                .orElse(null);

        List<String> halfSalesItemList = null;
        if(halfSalesPromotion != null){
            halfSalesItemList = halfSalesPromotion.getRelatedItems();
        }

        double prmotionDiscount = 0;
        double totalAmount = 0;
        boolean haveDiscount = false;
        String promotionString = "";

        for(String item : inputs){
            String[] splitResult = item.split("x");
            String itemCode = splitResult[0].trim();
            int itemQty = Integer.parseInt(splitResult[1].trim());

            Item temp = itemList.stream().filter(e -> e.getId().equals(itemCode))
                    .findFirst()
                    .orElse(null);

            if(temp!= null){
                result += temp.getName()+" x "+fmt(itemQty)+" = "+fmt(itemQty*temp.getPrice())+" yuan\n";
                totalAmount+=itemQty*temp.getPrice();

                if(halfSalesItemList != null && halfSalesItemList.contains(temp.getId())){
                    prmotionDiscount+=itemQty*temp.getPrice()/2;
                    orderedHalfPriceItemList.add(temp);
                }
            }
        }

        if(totalAmount >= 30 || prmotionDiscount!=0){
            if(prmotionDiscount > 6){
                String halfPriceItem = "";
                for(int i = 0; i<orderedHalfPriceItemList.size(); i++){
                    halfPriceItem += orderedHalfPriceItemList.get(i).getName();
                    if(i!=orderedHalfPriceItemList.size()-1){
                        halfPriceItem += "，";
                    }
                }
                promotionString += "Half price for certain dishes ("+halfPriceItem+")，saving "+fmt(prmotionDiscount)+" yuan\n";
            }else{
                promotionString += "Deduct 6 yuan when the order reaches 30 yuan, saving 6 yuan\n";
                prmotionDiscount = 6;
            }
            haveDiscount = true;
        }else{
            prmotionDiscount = 0;
        }
        result += "-----------------------------------\n";
        if(haveDiscount){
            result += "Promotion used:\n";
            result += promotionString;
            result += "-----------------------------------\n";
        }
        result += "Total："+fmt(totalAmount-prmotionDiscount)+" yuan\n";
        result += "===================================";

        return result;
    }
}
