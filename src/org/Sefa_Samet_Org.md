# Organisation Katmanı - Savaş Simülasyonu Eklemeleri

## Genel Bakış

Projenin eski halinde Organisation katmanı sadece **yaşam döngüsü** (feeding, swarmRenewal, temperatureControl) içeriyordu. Yeni eklenen Wasp vs Sentinel savaş simülasyonunu desteklemek için bu katmanı genişlettik.

---

## Eklenen Scheme: `doBattle`

```xml
<scheme id="doBattle">
    <goal id="hiveDefense" type="maintenance">
        <plan operator="parallel">
            <goal id="patrolPerimeter">
                <plan operator="sequence">
                    <goal id="moveToPatrolPoint"/>
                    <goal id="scanForThreats"/>
                </plan>
            </goal>
            <goal id="detectThreats">
                <plan operator="choice">
                    <goal id="fleeToSafety"/>
                    <goal id="groupAttack"/>
                </plan>
            </goal>
        </plan>
    </goal>
</scheme>
```

### Goal Açıklamaları

| Goal | Açıklama | Plan Tipi |
|------|----------|-----------|
| `hiveDefense` | Ana savunma hedefi (sürekli) | maintenance |
| `patrolPerimeter` | Kovan çevresinde devriye | sequence |
| `detectThreats` | Tehdit algılama ve karar | choice |
| `fleeToSafety` | Tehlike anında kovana kaçış | - |
| `groupAttack` | 2+ sentinel ile karşı saldırı | - |

---

## Eklenen Mission: `mDefender`

```xml
<mission id="mDefender" min="1">
    <goal id="patrolPerimeter"/>
    <goal id="moveToPatrolPoint"/>
    <goal id="scanForThreats"/>
    <goal id="detectThreats"/>
    <goal id="fleeToSafety"/>
    <goal id="groupAttack"/>
</mission>
```

Bu mission, sentinel arılarına savaş sorumluluklarını atar.

---

## Eklenen Norm: `nDefender`

```xml
<norm id="nDefender" type="obligation" role="sentinel" mission="mDefender"/>
```

Bu norm sayesinde **sentinel rolündeki tüm arılar** `mDefender` mission'ına **zorunlu olarak** bağlanır.

---

## Üç Katman Uyumu

| Katman | Savaş Desteği |
|--------|---------------|
| **Org** | `doBattle` scheme, `mDefender` mission, `nDefender` norm |
| **Agt** | `patrol_area`, `flee_to_hive`, `sentinel_group_ready` planları |
| **Env** | `WaspArtifact`, `GeminiService`, counter-attack mekanikleri |

---

## Savaş Akış Diyagramı

```
hiveDefense (maintenance)
    │
    ├── patrolPerimeter [parallel]
    │       ├── moveToPatrolPoint
    │       └── scanForThreats
    │
    └── detectThreats [choice]
            ├── fleeToSafety (wasp yakında → kaç)
            └── groupAttack (2+ sentinel → saldır)
```

---

*Sefa Samet - Çok Etmenli Yapay Zeka Final Projesi*
