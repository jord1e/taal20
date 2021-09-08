package nl.jrdie.taal20.data

enum class KostenkaartField(val friendlyName: String, val type: KostenkaartType) {
    HARDWARE_KOMPAS("kompas", KostenkaartType.HARDWARE),
    HARDWARE_ZW_OOG("zwOog (alles is it behalve zwart)", KostenkaartType.HARDWARE),
    HARDWARE_KLEUR_OOG("kleurOog", KostenkaartType.HARDWARE),
    HARDWARE_VARIABLE("variable (a..z)", KostenkaartType.HARDWARE),
    VERBRUIK_STAP_VOORUIT("stapVooruit", KostenkaartType.VERBRUIK),
    VERBRUIK_STAP_ACHTERUIT("stapAchteruit", KostenkaartType.VERBRUIK),
    VERBRUIK_DRAAI_LINKS("draaiLinks", KostenkaartType.VERBRUIK),
    VERBRUIK_DRAAI_RECHTS("draaiRechts", KostenkaartType.VERBRUIK),
    VERBRUIK_ZW_OOG("zwOog", KostenkaartType.VERBRUIK),
    VERBRUIK_KLEUR_OOG("kleurOog", KostenkaartType.VERBRUIK),
    VERBRUIK_KOMPAS("kompas", KostenkaartType.VERBRUIK),
    VERBRUIK_DUW_OBSTAKEL_SCHADE("duw obstakel (schade)", KostenkaartType.VERBRUIK),
    VERBRUIK_TOEWIJZING_A_IS_1("toewijzing (a = 1) (het uitvoeren van een toekenning)", KostenkaartType.VERBRUIK),
    VERBRUIK_OPERATIE("operatie (+, 0, *, %, /)", KostenkaartType.VERBRUIK),
    VERBRUIK_VERGELIJKING("vergelijking (==, <, >, !=)", KostenkaartType.VERBRUIK),
    SOFTWARE_ZOLANG_LUS("zolang (lus)", KostenkaartType.SOFTWARE),
    SOFTWARE_ALS_KEUZE("als (keuze)", KostenkaartType.SOFTWARE),
    SOFTWARE_OPDRACHT("opdracht", KostenkaartType.SOFTWARE),
    SOFTWARE_TOEKENNING("toekenning", KostenkaartType.SOFTWARE),
    START_KAPITAAL("Start kapitaal", KostenkaartType.START_KAPITAAL)
}
