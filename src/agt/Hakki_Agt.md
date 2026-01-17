# Agent Katmanı Değişiklikleri (Wasp Savaş Sistemi)

Bu belge, projeye eklenen **Wasp vs Sentinel savaş** özelliği için Agent katmanında yapılan değişiklikleri açıklamaktadır.

---

## 1. Yeni Dosya: `wasp.asl` (Tamamı Yeni)

LLM destekli yırtıcı ajan. Sentinel arıları avlar.

### Başlangıç Durumu
```prolog
health(200).              // Başlangıç canı
alive(true).              // Yaşam durumu
battle_started(false).    // Savaş başlangıç kontrolü
```

### Ana Planlar

| Plan | Açıklama |
|------|----------|
| `+!start_hunt` | Ajan başlatma, WaspArtifact oluşturma |
| `+sentinel_count(N)` | Kalan sentinel sayısını takip |
| `+wasp_health(H)` | Can durumu ve kritik uyarılar |
| `+wasp_alive(false)` | Yenilgi mesajı (Sentineller kazandı) |
| `+battle_active(false)` | Zafer mesajı (Wasp kazandı) |
| `+attack_target(X,Y,Reasoning)` | LLM'den gelen strateji kararları |

---

## 2. Değişiklik: `worker.asl` (Eklenen Bölüm: 330-398)

### Yeni Bölüm: Wasp Savunma Sistemi

```asl
/* ---------- Wasp Defense System ---------- */
```

### Yeni Belief'ler
- `fleeing` - Sentinel kaçış durumunda
- `wasp_nearby` - Yakında wasp var
- `attacking_wasp` - Saldırı koordinasyonu

### Yeni Planlar

| Plan | Satır | Açıklama |
|------|-------|----------|
| `+wasp_detected(WX,WY)` | 333 | Wasp tespit edildiğinde kaçış başlat |
| `+!flee_to_hive` | 339 | Kovana kaçış planı |
| `+!patrol_area` | 363 | Kovan çevresinde devriye |
| `+wasp_gone` | 386 | Wasp gitti, normal görevlere dön |
| `+sentinel_group_ready(Count)` | 392 | 2+ sentinel → karşı saldırı |
| `+attacking_wasp` | 396 | Grup saldırısına katılım |

### Mevcut Planlara Eklenen Koşul

Isıtma/soğutma planlarına `not fleeing` koşulu eklendi:

```diff
-+!heat : not heating & energy(E) & not is_hungry(E) & role(sentinel)
++!heat : not heating & energy(E) & not is_hungry(E) & role(sentinel) & not fleeing
```

Bu sayede sentinel kaçarken ısıtma/soğutma yapmaz.

---

## Özet

| Dosya | Değişiklik Tipi | Satır Sayısı |
|-------|-----------------|--------------|
| `wasp.asl` | Yeni dosya | 56 |
| `worker.asl` | Ekleme | ~70 satır (330-398) |
