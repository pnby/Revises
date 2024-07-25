package me.pink.revises.utils

import org.bukkit.Material
import org.bukkit.inventory.ItemFlag
import org.bukkit.inventory.ItemStack
import org.bukkit.inventory.meta.ItemMeta


@Suppress("DEPRECATION")
class ItemBuilder(private val itemStack: ItemStack) {

    constructor(material: Material) : this(ItemStack(material))

    private val itemMeta: ItemMeta = itemStack.itemMeta ?: throw IllegalArgumentException("ItemMeta cannot be null")

    fun setName(name: String): ItemBuilder {
        itemMeta.setDisplayName(name)
        return this
    }

    fun setLore(lore: MutableList<String>): ItemBuilder {
        itemMeta.lore = lore
        return this
    }

    fun addLoreLine(line: String): ItemBuilder {
        val lore = itemMeta.lore ?: mutableListOf()
        lore.add(line)
        itemMeta.lore = lore
        return this
    }

    fun setAmount(amount: Int): ItemBuilder {
        itemStack.amount = amount
        return this
    }

    fun addEnchant(enchantment: org.bukkit.enchantments.Enchantment, level: Int, ignoreLevelRestriction: Boolean): ItemBuilder {
        itemMeta.addEnchant(enchantment, level, ignoreLevelRestriction)
        return this
    }

    fun addItemFlags(vararg flags: ItemFlag): ItemBuilder {
        itemMeta.addItemFlags(*flags)
        return this
    }

    fun setUnbreakable(unbreakable: Boolean): ItemBuilder {
        itemMeta.isUnbreakable = unbreakable
        return this
    }

    fun hideFlags(condition: Boolean): ItemBuilder {
        if (condition) {
            itemStack.addItemFlags(ItemFlag.HIDE_ENCHANTS)
        }
        return this
    }

    fun build(): ItemStack {
        itemStack.itemMeta = itemMeta
        return itemStack
    }
}
