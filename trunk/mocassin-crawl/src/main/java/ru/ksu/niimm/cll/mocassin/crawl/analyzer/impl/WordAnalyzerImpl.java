package ru.ksu.niimm.cll.mocassin.crawl.analyzer.impl;

import edu.uci.ics.jung.graph.Graph;
import org.apache.commons.collections.CollectionUtils;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.Reference;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.StructuralElement;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.WordAnalyzer;
import ru.ksu.niimm.cll.mocassin.crawl.analyzer.WordFeatureInfo;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class WordAnalyzerImpl implements WordAnalyzer {
    private String[] words = {"введ", "векторн", "возьм", "действ", "найд", "наконец",
            "напр", "означа", "основн", "подцеп", "показыва", "представля", "провер",
            "рассматрива", "рассужден", "связ", "базис", "достаточн", "неотрицательн",
            "оболочк", "однородн", "отмет", "очевидн", "пар", "перемен", "переход",
            "предположен", "приведен", "сильн", "индекс", "найдет", "обозначен",
            "объединен", "проективн", "слаб", "согласн", "соответствен", "частност",
            "единиц", "мерн", "монотон", "оценк", "помощ", "противореч", "различн",
            "связа", "собствен", "указа", "этот", "итак", "меньш", "последн", "правил",
            "предполож", "пример", "учет", "цел", "веществен", "докаж", "известн",
            "интегра", "предложен", "состо", "структур", "теор", "утвержден", "допуст",
            "единствен", "найдут", "относительн", "полож", "порожден", "следств",
            "транспозиц", "будет", "был", "двух", "координат", "крайн", "обознач",
            "особ", "перв", "прост", "свойств", "сторон", "всяк", "выполня", "действительн",
            "полаг", "преобразован", "принадлежат", "сопряжен", "ест", "независим",
            "нечетн", "положительн", "размерн", "разн", "строг", "част", "все", "подвид",
            "потенциа", "прич", "раз", "умножен", "друг", "замет", "класс", "связност",
            "такж", "индексн", "использу", "компонент", "набор", "соответств", "тепер",
            "формул", "групп", "имеют", "их", "когд", "подстановок", "дан", "доказа",
            "интегральн", "мер", "нул", "огранич", "равн", "систем", "через", "матриц",
            "назначен", "работ", "справедлив", "ассоциирова", "комплексн", "област",
            "показа", "совпада", "тривиальн", "вкладыва", "выпукл", "одн", "дал",
            "представлен", "результат", "аналогичн", "интегрирова", "определ", "рассмотр",
            "многообраз", "отсюд", "тольк", "выполн", "замкнут", "произвольн", "соотношен",
            "замечан", "откуд", "покаж", "получ", "можн", "непрерывн", "поверхн", "име",
            "неравенств", "поскольк", "значен", "конечн", "разрешим", "рост", "линейн",
            "определя", "полупрям", "числ", "некотор", "называ", "пространств", "существ",
            "удовлетворя", "форм", "ограничен", "семейств", "люб", "порожда", "поэт",
            "точк", "получа", "уравнен", "случа", "вектор", "вид", "элемент", "отображен",
            "следовательн", "равенств", "имеет", "кажд", "определен", "котор", "произведен",
            "цикл", "сил", "если", "доказательств", "образ", "услов", "множеств", "решен",
            "теорем", "функц", "след", "есл", "лемм", "подстановк", "всех", "явля", "где",
            "пуст", "задач", "тогд"};
    private List<String> wordList = new ArrayList<String>(Arrays.asList(words));

    @Override
    public List<WordFeatureInfo> extractWordFeatures(Graph<StructuralElement, Reference> graph) {
        ArrayList<WordFeatureInfo> result = new ArrayList<WordFeatureInfo>();
        ArrayList<StructuralElement> elements = new ArrayList<StructuralElement>(graph.getVertices());

        for (StructuralElement element: elements) {
            List<String> contents = element.getStemContents();
            List<String> intersection = new ArrayList<String>(CollectionUtils.intersection(contents, wordList));
            if (intersection.size() > 0) {
                WordFeatureInfo.Builder builder = new WordFeatureInfo.Builder(element);
                for (String stem: intersection) {
                    builder.word(stem);
                }
                result.add(builder.build());
            }
        }

        return result;
    }
}
