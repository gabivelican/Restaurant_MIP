Cum rulezi proiectul (pe Windows, în cmd.exe)
---------------------------------------------
0. Deschide un terminal (cmd.exe) și mută-te în directorul proiectului (acolo unde se află `pom.xml`). Exemplu (folosește această comandă exact în cmd.exe):

```cmd
cd /d "D:\Facultate\ANUL 2\SEM 1\MIP\Tema 1"
```

1. Compilează proiectul cu Maven:

```cmd
mvn -DskipTests package
```

2. Copiază dependințele runtime (prima dată sau după schimbarea dependențelor):

```cmd
mvn dependency:copy-dependencies -DincludeScope=runtime
```

3. Rulează aplicația (asigură-te că folosești `;` ca separator pe Windows):

```cmd
java -cp "target/classes;target/dependency/*" ro.unitbv.restaurant.Main
```

Ce vezi când rulezi
-------------------
Programul afișează patru secțiuni (Iteration 1..4):
- Iteration 1: afișează un meniu simplu cu produse (denumire, preț și gramaj/volum).
- Iteration 2: exemplu de comandă (adăugare produse, calcul total cu TVA și aplicare discount "Happy Hour").
- Iteration 3: interogări pe meniu (produse vegetariene sortate, preț mediu pentru desert, căutare sigură) și construcția unei pizza custom prin Builder.
- Iteration 4: citirea fișierului de configurare `config.json` și exportul meniului în `menu_export.json`.

Structura principală a proiectului și ce face fiecare fișier
---------------------------------------------------------
(Următoarele explicații sunt foarte simple — gândește-te la ele ca la o poveste despre cum funcționează programul.)

- `Main.java` — punctul de pornire. Când rulezi programul, Java pornește aici. Am adunat toate pașii de demonstrație (iterațiile 1..4) în acest fișier ca să fie ușor de arătat profesorului. Vei vedea declarații de tipul:
  - `Food pizza = new Food("Pizza Carbonara", 52.5, 400, false);`
    - Asta înseamnă: "creez o mâncare numită Pizza Carbonara, costă 52.5 RON, cântărește 400g și NU este vegetariană".
  - Motivul pentru care scriem așa: vrem să creăm obiecte (produse) înainte să le punem în meniu sau în comandă.

- `Product.java` (clasa de bază) — reprezintă orice produs din meniu. Are două lucruri importante: `name` (nume) și `price` (preț). Clasele `Food` și `Drink` moștenesc aceste caracteristici.
  - De obicei are și o metodă `toString()` care transformă obiectul în text frumos pentru afișare.

- `Food.java` — clasa pentru mâncare (ex: pizza, paste, salată).
  - Are un câmp în plus numit `weight` (gramaj, ex: 450g).
  - Constructorul primește `name`, `price`, `weight` și un `isVegetarian` (adevărat/fals).
  - Suprascrie `toString()` ca să afișeze: "Pizza Margherita - 45.0 RON - Gramaj: 450g".

- `Drink.java` — clasa pentru băuturi (ex: limonadă, apă, vin).
  - Are un câmp în plus numit `volume` (ex: 500ml) și un `isAlcoholic` (adevărat/fals).
  - `toString()` afișează: "Limonada - 15.0 RON - Volum: 400ml".

- `Category.java` — enum sau clasă care conține categoriile meniului (de exemplu: MAIN_COURSE, DESSERT, SOFT_DRINK, ALCOHOLIC_DRINK).

- `Menu.java` — clasa care ține listele de produse organizate pe categorii.
  - Metode importante:
    - `addProduct(Product p, Category c)` — adaugă un produs în categoria potrivită.
    - `printMenu(...)` — afișează meniul frumos pe categorii.
    - `printMenuIteration1(String name)` — afișare simplificată folosită în Iteration 1.
    - `getVegetarianSorted()` — returnează lista produselor vegetariene sortate alfabetic.
    - `getAveragePrice(Category c)` — calculează prețul mediu pentru categoria dată.
    - `existsProductMoreExpensiveThan(double price)` — verifică dacă e vreun produs mai scump decât o valoare.
    - `findProductByName(String name)` — caută un produs și întoarce `Optional<Product>` (adică: ori găsește produsul, ori întoarce nimic, fără să crape programul).

- `Order.java` — clasa care reprezintă o comandă la masă.
  - Ține o colecție (de ex. `Map<Product, Integer>`) care spune: ce produs și câte bucăți.
  - Metode importante:
    - `addProduct(Product p, int quantity)` — adaugă un produs în comandă.
    - `calculateTotalWithVAT()` — calculează totalul comenzii cu TVA (TVA = 9% în tema originală, dar în Iteration 4 îl citim din `config.json`).
    - `calculateTotalWithDiscount(DiscountRule rule)` — primește o regulă (o funcție) care poate aplica un discount la total. Exemplu: "Happy Hour".

- `DiscountRule.java` — o interfață funcțională (adică: poți trimite o funcție/lambda) care modifică totalul.
  - Ex: `double apply(double totalWithVAT, Map<Product,Integer> items)` — primește totalul și lista de produse și întoarce noul total după discount.

- `Pizza.java` — clasă specială pentru pizza, construită cu pattern-ul Builder.
  - `Pizza.Builder` impune setarea elementelor obligatorii (nume, preț, tip blat, tip sos) și permite adăugarea unor topping-uri opționale.
  - Motivul: pizza are multe combinații și vrem să construim obiectul în pași clari.

- `AppConfig.java` și `ConfigLoader.java` — gestionează citirea fișierului `config.json` (de ex: numele restaurantului și TVA-ul).
  - `ConfigLoader.load("src/main/resources/config.json")` încearcă să citească și să parseze JSON-ul. Dacă fișierul lipsește sau are eroare, `ConfigLoader` returnează `null` sau aruncă o excepție pe care o prindem și afișăm un mesaj prietenos.

- `MenuExporter.java` — exportă meniul în JSON într-un fișier (de ex: `menu_export.json`).
  - Folosește Gson pentru serializare.
  - Are grijă să trateze erori la scriere (de ex: dacă nu poate crea fisierul, afișează un mesaj clar).

De ce am pus toată logica în `Main.java` (la tine în versiunea finală)
-------------------------------------------------------------------
- Profesorii vor vedea ușor toate demonstrațiile într-un singur fișier.
- E mai ușor să arăți cum funcționează fiecare iterație: deschizi `Main.java`, execuți programul și explici fiecare bloc.
- Dacă vrei, putem oricând separa iar iterațiile în fișiere diferite pentru demonstrații individuale.

Explicații simple pentru întrebările frecvente pe care ți le poate pune profesorul
---------------------------------------------------------------------------------
Întrebare: "De ce creezi obiecte așa: `new Food(... )`?"
Răspuns simplu: pentru că vrem să avem un produs în memorie. Un obiect reprezintă o pizza/limonadă în program. Acum putem să-l punem în meniu sau în comandă.

Întrebare: "De ce `Order` folosește un `Map<Product,Integer>`?"
Răspuns simplu: ca să știm câte bucăți din fiecare produs a comandat clientul. De exemplu: 2 pizza, 1 limonadă.

Întrebare: "De ce `calculateTotalWithDiscount` primește o regulă?"
Răspuns simplu: ca să putem schimba regula oricând fără să schimbăm codul comenzii. Astăzi e "Happy Hour", mâine poate fi o altă promoție.

Întrebare: "Ce fac dacă adaug într-o comandă `quantity = 0`?"
Răspuns recomandat: `quantity = 0` înseamnă că nu vrem să adăugăm nicio bucată din produs. În codul actual nu e o eroare fatală — dar ideal ar fi să filtrăm astfel de adăugări (sau să afișăm un mesaj) pentru claritate.

Întrebare: "Cum se face că nu crăpăm dacă config.json lipsește sau e corupt?"
Răspuns simplu: `ConfigLoader` încearcă să citească fișierul într-un bloc `try-catch`. Dacă fișierul lipsește (FileNotFoundException) sau e invalid (JsonSyntaxException), programul prinde excepția și afișează un mesaj prietenos în loc să arate stack trace.

Ce să arăți profesorului când rulezi (pași simpli)
-------------------------------------------------
1. Deschide `Main.java` și spune: "Aici pornește programul. Vezi cum am grupat demonstrațiile pentru iterațiile 1..4?"
2. Rulează comanda de mai sus în cmd.exe.
3. Arată secțiunea Iteration 1 și spune: "Aici creez produse și le afișez. Exemplu: `Food pizza = new Food(\"Pizza Carbonara\", 52.5, 400, false);` — asta e pizza cu nume, preț, gramaj și o informație dacă e vegetariană sau nu."
4. Arată Iteration 2 și explică comanda și calculul total cu TVA și discount.
5. Arată Iteration 3 și explică interogările (filtrare, sortare, medii) și Builder pentru pizza.
6. Arată Iteration 4 și explică cum citim `config.json` și exportăm meniul.

Unde se găsesc fișiere importante
---------------------------------
- `src/main/resources/config.json` — fișierul de configurare (numele restaurantului și TVA).
- `menu_export.json` — fișier creat la rulare (exportul meniului).
- `pom.xml` — lista dependențelor (aici e Gson pentru lucru cu JSON).

Ce poți spune despre deciziile de design (pe scurt, dar clar)
-----------------------------------------------------------
- Moștenire: `Food` și `Drink` moștenesc `Product` pentru a evita duplicarea codului (nume + preț apar pentru ambele).
- Polimorfism: `toString()` este suprascris în clasele derivate pentru a afișa informații specifice (gramaj/volum).
- Strategy / Lambda: folosim o interfață `DiscountRule` pentru a putea trimite reguli dinamice la `Order`.
- Builder: `Pizza.Builder` simplifică crearea obiectelor complexe cu mulți parametri opționali.
- Robustete: folosim `Optional` pentru căutări care pot să nu returneze nimic, și blocuri `try-catch` pentru citirea JSON-ului.

Răspuns rapid la ce ar putea întreba profesorul (scurt)
-----------------------------------------------------
- "Cum calculezi TVA?" — `Order` aplică 9% (sau valoarea din `config.json`) peste subtotal.
- "De ce `equals()`/`hashCode()` la `Product`?" — pentru a folosi `Product` ca și cheie într-un `Map` corect.
- "Putem adăuga alte tipuri de produse?" — designul actual acceptă doar `Food` și `Drink`. Pentru a bloca extinderile nedorite am putea folosi clase `sealed` (Java 17+) sau construim fabrica internă.

Dacă vrei, îți fac și:
- un mic `README` în limba engleză (dacă trebuie la predare),
- sau un fat-jar (comandă Maven Shade) ca să rulezi cu `java -jar` ușor.

Sper că acest fișier te ajută să explici totul profesorului. Dacă vrei, pot scurta sau extinde secțiunile (de exemplu: explicații pas-cu-pas pentru fiecare metodă `toString`, fiecare linie din `Order`, sau exemple concrete de `config.json`).
